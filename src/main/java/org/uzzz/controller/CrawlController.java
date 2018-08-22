package org.uzzz.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.uzzz.utils.CsdnCrawler;

@RestController
@RequestMapping("crawl")
public class CrawlController {

	@Autowired
	private CsdnCrawler crawler;

	@GetMapping("blockchain")
	public String blockchain() throws IOException {

		return crawler.blockchain();
	}

}
