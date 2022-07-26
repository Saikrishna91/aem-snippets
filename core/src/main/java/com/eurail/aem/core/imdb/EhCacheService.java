package com.eurail.aem.core.imdb;

import org.json.JSONObject;

public interface EhCacheService {
	
	public String getMaxElementsCount();
	public String getTimeToIdleSeconds();
	public String getTimeToLiveSeconds();
	public void putObjToCache(CacheProps cacheProps, String cacheIdentifier, JSONObject resultsJSON);
	public JSONObject getObjFromCache(CacheProps cacheProps, String cacheIdentifier);

}
