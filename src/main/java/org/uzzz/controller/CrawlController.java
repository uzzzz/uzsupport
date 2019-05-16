package org.uzzz.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.uzzz.crawlers.CsdnCrawler;

@Controller
@RequestMapping("csdn")
public class CrawlController {

	@Autowired
	private CsdnCrawler csdnCrawler;

	@GetMapping("crawl_all")
	@ResponseBody
	public String crawl_all() throws IOException {
		csdnCrawler.crawl_all();
		return "OK";
	}

	@GetMapping("url")
	public String url(String url) throws IOException {
		long id = csdnCrawler.url(url);
		if (id == 0) {
			return "redirect:https://uzshare.com/";
		} else {
			return "redirect:https://uzshare.com/view/" + id;
		}
	}

	@GetMapping("search")
	@ResponseBody
	public String search(String key, //
			@RequestParam(required = false, defaultValue = "1") int start, //
			@RequestParam(required = false, defaultValue = "5") int end) throws IOException {
		csdnCrawler.crawl_search(key, start, end);
		return "OK";
	}
}
