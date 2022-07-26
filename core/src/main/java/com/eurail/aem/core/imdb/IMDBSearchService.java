package com.eurail.aem.core.imdb;

import org.apache.sling.api.resource.ResourceResolver;

public interface IMDBSearchService {
	
	public String getSearchAPIUrl();
	
	public String getAdvancedSearchAPIUrl();
	
	public String getYoutubePIUrl();
	
	public String getSearchAPIKey();
	
	public boolean isFileBasedCacheEnabled();
	
	public String getIMDBAPIConnection(ResourceResolver rr, boolean normalSearch, boolean findIMDBId, String searchKeyword, String imdbVideoID);
	
	public String getCachedResultsFromFile(String imdbVideoID, String searchKeyword, ResourceResolver resourceResolver);

}
