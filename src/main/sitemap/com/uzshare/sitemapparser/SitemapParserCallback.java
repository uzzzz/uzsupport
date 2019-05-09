package com.uzshare.sitemapparser;

public interface SitemapParserCallback {

	default void sitemap(String sitemap) {
	}

	void url(String url);
}
