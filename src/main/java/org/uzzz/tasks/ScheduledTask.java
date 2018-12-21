package org.uzzz.tasks;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.uzzz.crawlers.CsdnCrawler;

@Component
public class ScheduledTask {

	@Autowired
	private CsdnCrawler crawler;

	@Scheduled(initialDelay = 2000, fixedDelay = 1000 * 60 * 10)
	public void crawl_blockchain() throws IOException {
		System.out.println("crawl blockchain @Scheduled");
		crawler.blockchain();
	}

	@Scheduled(initialDelay = 2000, fixedDelay = 1000 * 60 * 10)
	public void crawl_careerlife() throws IOException {
		System.out.println("crawl careerlife @Scheduled");
		crawler.blockchain();
	}
}