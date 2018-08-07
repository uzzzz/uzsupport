package org.uzzz.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.uzzz.utils.Utils;

@RestController
@RequestMapping("crawl")
public class CrawlController {

	@GetMapping("blockchain")
	public String blockchain() throws IOException {

		return Utils.blockchain();
	}
}
