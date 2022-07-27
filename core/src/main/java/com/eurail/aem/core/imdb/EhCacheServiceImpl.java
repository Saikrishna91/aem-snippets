package com.eurail.aem.core.imdb;

import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, service = EhCacheService.class)
@Designate(ocd = EhCacheConfig.class)
public class EhCacheServiceImpl implements EhCacheService {
	
	private static final Logger LOG = LoggerFactory.getLogger(EhCacheServiceImpl.class);
	
	EhCacheConfig config;
	
	@Activate
	@Modified
	protected void activate(EhCacheConfig config) {
		this.config = config;
	}

	@Override
	public String getMaxElementsCount() {
		return config.maxElementsCount();
	}

	@Override
	public String getTimeToIdleSeconds() {
		return config.timeToIdle();
	}

	@Override
	public String getTimeToLiveSeconds() {
		return config.timeToLive();
	}
	
	/* Setting the JSONResults in MemoryCache as key-value pairs */
	public void putObjToCache(CacheProps cacheProps, String cacheIdentifier, JSONObject resultsJSON) {
		CacheFactoryInstance.getInstance().add(setCacheProps(cacheProps), cacheIdentifier, resultsJSON);
	}
	
	/* Getting JSONResults from MemoryCache as key-value pairs */
	public JSONObject getObjFromCache(CacheProps cacheProps, String cacheIdentifier) {
		JSONObject jsonObject = null;
		Object object = CacheFactoryInstance.getInstance().get(setCacheProps(cacheProps), cacheIdentifier);
		if(object != null && object instanceof JSONObject) {
			jsonObject = (JSONObject) object;
			LOG.info("Printing results from Cache : {}", jsonObject.toString());
		}
		return jsonObject;
	}
	
	private CacheProps setCacheProps(CacheProps cacheProps) {
		if(cacheProps.getMaxElements() == 0) {
			cacheProps.setMaxElements(Integer.parseInt(config.maxElementsCount()));
		} 
		if(cacheProps.getTimeToIdle() == 0) {
			cacheProps.setTimeToIdle(Integer.parseInt(config.timeToIdle()));
		}
		if(cacheProps.getTimeToLiveSeconds() == 0) {
			cacheProps.setTimeToLiveSeconds(Integer.parseInt(config.timeToLive()));
		}
		return cacheProps;
	}

}
