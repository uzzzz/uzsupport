package org.uzzz.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.uzzz.crawlers.CsdnCrawler;

@Controller
@RequestMapping("crawl")
public class CrawlController {

	@Autowired
	private CsdnCrawler crawler;

	@GetMapping("url")
	public String url(String url) throws IOException {
		long id = crawler.url(url);
		if (id == 0) {
			return "redirect:https://uzshare.com/";
		} else {
			return "redirect:https://uzshare.com/view/" + id;
		}
	}
}
