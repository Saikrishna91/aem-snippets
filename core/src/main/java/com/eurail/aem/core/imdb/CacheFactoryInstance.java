package com.eurail.aem.core.imdb;

import java.util.HashMap;
import java.util.Map;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheFactoryInstance {

	private static final Logger LOG = LoggerFactory.getLogger(CacheFactoryInstance.class);

	private static final CacheFactoryInstance INSTANCE = new CacheFactoryInstance();

	private static final Map<String, SiteCache> siteCacheMap = new HashMap<String, SiteCache>();

	public static CacheFactoryInstance getInstance() {
		return INSTANCE;
	}

	private synchronized SiteCache getCache(CacheProps cacheProps) {
		if (cacheProps == null || cacheProps.getCacheName() == null) {
			return null;
		}
		SiteCache siteCache = siteCacheMap.get(cacheProps.getCacheName());
		if (siteCache == null) {
			siteCache = new SiteCache(cacheProps);
			siteCacheMap.put(cacheProps.getCacheName(), siteCache);
		}
		return siteCache;
	}

	public synchronized void removeSiteCache(CacheProps cacheProps) {
		String cacheName = cacheProps.getCacheName();
		LOG.info("inside remove SITE cache  " + cacheName);
		SiteCache siteCache = siteCacheMap.get(cacheName);
		if (siteCache != null) {
			CacheManager cacheManager = CacheManager.getInstance();
			synchronized (cacheManager) {
				cacheManager.removeCache(cacheName);
			}
			siteCacheMap.remove(cacheName);
		}
	}

	public synchronized boolean add(CacheProps cacheProps, String cacheObjectName, Object cacheObject) {
		boolean isAdded = true;
		SiteCache siteCache = getCache(cacheProps);
		if (siteCache == null || cacheObject == null || cacheObjectName == null) {
			isAdded = false;
		} else {
			try {
				Element cacheElement = new Element(cacheObjectName, cacheObject);
				siteCache.putElement(cacheElement);
			} catch (CacheException e) {
				LOG.warn("Couldn't add duplicate object to cache: " + cacheObject.toString(), e);
			}

		}
		return isAdded;
	}
	
	public synchronized Object get(CacheProps cacheProps, String cacheObjectName) {
		SiteCache siteCache = getCache(cacheProps);
		if (siteCache != null) {
			Element element = siteCache.getElement(cacheObjectName);
			if (element != null) {
				LOG.info("GET:: Created " + element.getCreationTime() + " Accessed " + element.getLastAccessTime()
						+ " Updated " + element.getLastUpdateTime() + " Expire " + element.getExpirationTime());
				// }
				return element.getObjectValue();
			}
		}
		return null;
	}
}
