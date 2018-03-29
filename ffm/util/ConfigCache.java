package gov.hhs.cms.base.common.util;

public class ConfigCache {
	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public long getLastUpdatedCacheEntry() {
		return lastUpdatedCacheEntry;
	}

	public void setLastUpdatedCacheEntry(long lastUpdatedCacheEntry) {
		this.lastUpdatedCacheEntry = lastUpdatedCacheEntry;
	}

	private long lastUpdatedCacheEntry;
	private long lastModified;
	private String contents;
}
