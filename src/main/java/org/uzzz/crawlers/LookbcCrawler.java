package org.uzzz.crawlers;

import org.springframework.stereotype.Component;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;

@Component
public class LookbcCrawler extends BreadthCrawler {

	public LookbcCrawler() {
		super("crawl", true);
		addSeed("http://www.lookbc.com/");
		addRegex(
				"-.*(\\.(css|js|bmp|gif|jpe?g|JPE?G|png|tiff?|ico|nef|raw|mid|mp2|mp3|mp4|wav|wma|flv|mpe?g|avi|mov|mpeg|ram|m4v|wmv|rm|smil|pdf|doc|docx|pub|xls|xlsx|vsd|ppt|pptx|swf|zip|rar|gz|bz2|7z|bin|xml|txt|java|c|cpp|exe))$");
		setThreads(50);
		setTopN(100);
	}

	@Override
	public void visit(Page page, CrawlDatums next) {
		if (page.matchUrl("http://www.lookbc.com/read.php?.*")) {

		}
	}
}