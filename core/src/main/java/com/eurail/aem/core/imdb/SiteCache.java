package com.eurail.aem.core.imdb;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Searchable;
import net.sf.ehcache.search.Query;
import net.sf.ehcache.search.Result;
import net.sf.ehcache.search.Results;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;

public class SiteCache {

	private static final Logger LOG = LoggerFactory.getLogger(SiteCache.class);

	private Ehcache cache;

	public SiteCache(CacheProps cacheProps) {
		createSiteCache(cacheProps);
	}

	void createSiteCache(CacheProps cacheProps) {
		try {
			LOG.info("creating cache");
			LOG.info("SECURITY POLICY : " + System.getProperty("java.security.policy"));

			if (cacheProps != null) {
				LOG.info("starting to create cache...");
				CacheConfiguration cacheConfiguration = new CacheConfiguration(cacheProps.getCacheName(),
						cacheProps.getMaxElements());
				cacheConfiguration.setEternal(false);
				cacheConfiguration.setTimeToIdleSeconds(cacheProps.getTimeToIdle());
				cacheConfiguration.setTimeToLiveSeconds(cacheProps.getTimeToLiveSeconds());
				cacheConfiguration.setOverflowToDisk(false);
				// Add the below lines to make it searchable
				Searchable searchableConf = new Searchable();
				// We only need KEY search so lets disable value searching so we don't create an
				// index for values.
				searchableConf.setValues(false);
				cacheConfiguration.addSearchable(searchableConf);
				cache = new Cache(cacheConfiguration);
				LOG.info("created cache: " + cache);
				CacheManager cacheManager = CacheManager.getInstance();
				cacheManager.removeCache(cacheProps.getCacheName());
				cacheManager.addCache(cache);
			}
		} catch (CacheException e) {
			LOG.info("---------CACHE CREATION EXCEPTION-------------- ", e);
		}
	}

	boolean removeElement(Object key) {
		if (key != null && cache != null) {
			synchronized (cache) {
				if (cache.isKeyInCache(key)) {
					cache.remove(key);
					return true;
				}
			}
		}
		return false;
	}

	boolean putElement(Element element) {
		if (element != null && cache != null) {
			synchronized (cache) {
				if (!cache.isKeyInCache(element.getObjectKey())) {
					cache.put(element);
					return true;
				}
			}
		}
		return false;
	}

	Element getElement(Object id) {
		if (id != null && cache != null) {
			synchronized (cache) {
				if (cache.isKeyInCache(id)) {
					return cache.get(id);
				}
			}
		}
		return null;
	}

	public boolean removeAllElements(String id) {
		boolean isDeleted = false;
		boolean isAllDeleted = true;
		if (id != null && cache != null) {
			synchronized (cache) {
				List<String> list = cache.getKeys();
				Iterator<String> itrKeys = list.iterator();
				String key = null;
				if (itrKeys.hasNext()) {
					key = itrKeys.next();
					LOG.info(key + "=======" + id);
					if (key.contains(id)) {

						isDeleted = removeElement(key);
						LOG.info("isDeleted>>>" + isDeleted);
						if (!isDeleted) {
							isAllDeleted = isDeleted;
						}
					}
				}
			}
		}
		return isAllDeleted;
	}

	boolean updateElement(Element element) {
		if (element != null && cache != null) {
			synchronized (cache) {
				cache.put(element);
			}
			return true;
		}
		return false;
	}

	boolean touchCacheObject(String id) {
		if (id != null && cache != null) {
			synchronized (cache) {
				if (cache.isKeyInCache(id)) {
					Element element = cache.get(id);
					if (LOG.isDebugEnabled()) {
						LOG.debug("touchCacheObject:: Created " + element.getCreationTime() + " Accessed "
								+ element.getLastAccessTime() + " Updated " + element.getLastUpdateTime() + " Expire "
								+ element.getExpirationTime());
					}

					return true;
				}
			}
		}
		return false;
	}

	public int invalidateCacheItems(String searchString) {
		int itemRemovedCount = 0;
		if (searchString != null && searchString.trim().length() > 0) {
			synchronized (cache) {
				Query createQuery = cache.createQuery();
				createQuery.includeKeys();
				createQuery.addCriteria(Query.KEY.ilike(searchString)).end();
				Results results = createQuery.execute();
				String key = null;

				for (Result result : results.all()) {
					try {
						key = (String) result.getKey();
						cache.remove(result.getKey());
						itemRemovedCount++;
					} catch (IllegalStateException e) {
						// Just need to catch this and ignore,
						// this is possible if the items is removed between the search
						// and remove.
						LOG.error("Unable to remove cache:Key " + key);
					}
				}
			}
		}
		return itemRemovedCount;
	}

	int getTimeToLive() {
		return (int) cache.getCacheConfiguration().getTimeToLiveSeconds();
	}
}
