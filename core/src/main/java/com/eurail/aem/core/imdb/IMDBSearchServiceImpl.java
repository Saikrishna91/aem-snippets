package com.eurail.aem.core.imdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;

@Component(immediate = true, service = IMDBSearchService.class)
@Designate(ocd = IMDBSearchConfig.class)
public class IMDBSearchServiceImpl implements IMDBSearchService {

	private static final Logger LOG = LoggerFactory.getLogger(IMDBSearchServiceImpl.class);

	private IMDBSearchConfig config;
	
	@Reference
	EhCacheService ehCacheService;

	@Activate
	@Modified
	protected void activate(IMDBSearchConfig config) {
		this.config = config;
	}

	@Override
	public String getSearchAPIUrl() {
		return config.searchAPIUrl();
	}

	@Override
	public String getAdvancedSearchAPIUrl() {
		return config.advancedSearchAPIUrl();
	}

	@Override
	public String getYoutubePIUrl() {
		return config.youtubeAPIUrl();
	}

	@Override
	public String getSearchAPIKey() {
		return config.searchAPIKey();
	}
	
	@Override
	public boolean isFileBasedCacheEnabled() {
		return config.fileBasedCache();
	}

	@Override
	public String getIMDBAPIConnection(ResourceResolver rr, boolean normalSearch, boolean findIMDBId, String searchKeyword, String imdbVideoID) {
		StringBuilder response = new StringBuilder();
		JSONObject responseObj = new JSONObject();
		try {
			HttpURLConnection connection = null;
			//Generate Connection URL for NormalSearch Flow:
			if (normalSearch && findIMDBId && imdbVideoID == null) {
				connection = (HttpURLConnection) new URL(config.searchAPIUrl() + config.searchAPIKey() + "/" + searchKeyword).openConnection();
			}
			//Generate Connection URL for AdvancedSearch Flow:
			if (!normalSearch && findIMDBId && imdbVideoID == null) {
				connection = (HttpURLConnection) new URL(config.advancedSearchAPIUrl() + config.searchAPIKey() + "?" + searchKeyword).openConnection();
			}
			//Generate Connection URL to Video URL:
			if(imdbVideoID != null) {
				connection = (HttpURLConnection) new URL(config.youtubeAPIUrl() + config.searchAPIKey() + "/" + imdbVideoID).openConnection();
			}
			connection.setRequestMethod(Constants.METHOD_GET);
			connection.setConnectTimeout(20000);
			connection.setReadTimeout(20000);
			connection.setRequestProperty(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
			connection.addRequestProperty(Constants.USER_AGENT, "Mozilla/4.0");
			boolean isError = connection.getResponseCode() >= 400;
			InputStream is = isError ? connection.getErrorStream() : connection.getInputStream();
			try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
				String responseLine = null;
				while ((responseLine = br.readLine()) != null) {
					response.append(responseLine.trim());
				}
			}
			responseObj = new JSONObject(response.toString());
			if(config.fileBasedCache() && StringUtils.isNotEmpty(responseObj.toString())) {
				setResultsToFileCache(rr, searchKeyword, imdbVideoID, responseObj);
			}
			if(!config.fileBasedCache() && StringUtils.isNotEmpty(responseObj.toString())) {
				setResultsToMemoryCache(rr, searchKeyword, imdbVideoID, responseObj);
			}
		} catch (IOException | JSONException e) {
			LOG.error("Error Occured during fetching IMDB Connection : {}", e.getMessage());
		}
		return responseObj.toString();
	}
	
	@Override
	public String getCachedResultsFromFile(String imdbVideoID, String searchKeyword, ResourceResolver resourceResolver) {
		if(config.fileBasedCache()) {
			return imdbVideoID == null ? getCachedResults(searchKeyword, resourceResolver) : getCachedResults(imdbVideoID, resourceResolver);
		}
		if(!config.fileBasedCache()) {
			return imdbVideoID == null ? getCachedResultsFromMemory(searchKeyword) : getCachedResultsFromMemory(imdbVideoID);
		}
		return "";
	}
	
	public String getCachedResultsFromMemory(String searchKeyword) {
		String resultJSON = "";
		CacheProps cacheProps = new CacheProps();
		if(!ehCacheService.getMaxElementsCount().isEmpty()) {
			cacheProps.setMaxElements(Integer.parseInt(ehCacheService.getMaxElementsCount()));
		}
		if(!ehCacheService.getTimeToIdleSeconds().isEmpty()) {
			cacheProps.setTimeToIdle(Integer.parseInt(ehCacheService.getTimeToIdleSeconds()));
		}
		if(!ehCacheService.getTimeToLiveSeconds().isEmpty()) {
			cacheProps.setTimeToLiveSeconds(Integer.parseInt(ehCacheService.getTimeToLiveSeconds()));
		}
		searchKeyword = searchKeyword.replaceAll(" ", "_");
		cacheProps.setCacheName(searchKeyword.toLowerCase());
		JSONObject result = ehCacheService.getObjFromCache(cacheProps, searchKeyword);
		if(result != null) {
			resultJSON = result.toString();
		}
		return resultJSON;
	}
	
	public String getCachedResults(String searchKeyword, ResourceResolver resourceResolver) {
		String resultJSON = "";
		try {
			searchKeyword = searchKeyword.replaceAll(" ", "_");
			Resource damPathResource = resourceResolver.getResource(Constants.DAM_PATH);
			if (!Objects.isNull(damPathResource) && damPathResource.hasChildren() && !Objects.isNull(damPathResource.getChild(searchKeyword.toLowerCase()))) {
				Asset asset = damPathResource.getChild(searchKeyword.toLowerCase()).adaptTo(Asset.class);
				Resource original = asset.getOriginal();
				InputStream content = original.adaptTo(InputStream.class);
				StringBuilder sb = new StringBuilder();
				String line;
				BufferedReader br = new BufferedReader(new InputStreamReader(content, StandardCharsets.UTF_8));
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				resultJSON = new JSONObject(sb.toString()).toString();
			} 
		} catch(JSONException | IOException e) {
			LOG.error("Error occured while getting cached Results JSON : {}", e.getMessage());
		}
		return resultJSON;
	}
	
	private void setResultsToFileCache(ResourceResolver resourceResolver, String searchKeyword, String imdbVideoID, JSONObject responseObj) {
		Resource damPathResource = resourceResolver.getResource(Constants.DAM_PATH);
		if (!Objects.isNull(damPathResource) && ((!damPathResource.hasChildren()) || (damPathResource.hasChildren() && Objects.isNull(damPathResource.getChild(searchKeyword.toLowerCase()))))) {
			searchKeyword = imdbVideoID == null ? searchKeyword.replaceAll(" ", "_") : imdbVideoID;
			searchKeyword = Constants.DAM_PATH + "/" + searchKeyword.toLowerCase();
			InputStream is = IOUtils.toInputStream(responseObj.toString(), StandardCharsets.UTF_8);
			AssetManager assetMgr = resourceResolver.adaptTo(AssetManager.class);
			assetMgr.createAsset(searchKeyword, is, "application/json", true);
		}
	}
	
	private void setResultsToMemoryCache(ResourceResolver resourceResolver, String searchKeyword, String imdbVideoID, JSONObject responseObj) {
		CacheProps cacheProps = new CacheProps();
		searchKeyword = imdbVideoID == null ? searchKeyword.replaceAll(" ", "_") : imdbVideoID;
		ehCacheService.putObjToCache(cacheProps, searchKeyword.toLowerCase(), responseObj);
	}

}
