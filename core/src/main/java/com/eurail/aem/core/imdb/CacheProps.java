package com.eurail.aem.core.imdb;

public class CacheProps {

	private String cacheName;
	private int timeToIdle;
	private int timeToLiveSeconds;
	private boolean overflowToDisk;
	private int maxElements;

	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	public int getTimeToIdle() {
		return timeToIdle;
	}

	public void setTimeToIdle(int timeToIdle) {
		this.timeToIdle = timeToIdle;
	}

	public int getTimeToLiveSeconds() {
		return timeToLiveSeconds;
	}

	public void setTimeToLiveSeconds(int timeToLiveSeconds) {
		this.timeToLiveSeconds = timeToLiveSeconds;
	}

	public boolean isOverflowToDisk() {
		return overflowToDisk;
	}

	public void setOverflowToDisk(boolean overflowToDisk) {
		this.overflowToDisk = overflowToDisk;
	}

	public int getMaxElements() {
		return maxElements;
	}

	public void setMaxElements(int maxElements) {
		this.maxElements = maxElements;
	}

}
