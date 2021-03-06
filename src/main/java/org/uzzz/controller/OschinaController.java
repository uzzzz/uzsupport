package org.uzzz.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.uzzz.crawlers.OschinaCrawler;

@Controller
@RequestMapping("oschina")
public class OschinaController {

	@Autowired
	private OschinaCrawler crawler;

	@GetMapping("url")
	public String url(String url) throws IOException {
		long id = crawler.url(2, url);
		if (id == 0) {
			return "redirect:https://uzshare.com/";
		} else {
			return "redirect:https://uzshare.com/view/" + id;
		}
	}

	@GetMapping("crawl_all")
	@ResponseBody
	public String crawl_all(int cid, String classification, int start, int end) throws IOException {
		crawler.crawl_all(cid, classification, start, end);
		return "OK";
	}
}
