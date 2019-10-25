package org.uzzz.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.uzzz.crawlers.UzzzCsdnCrawler;

@Controller
@RequestMapping("uzzz")
public class UzzzController {

	@Autowired
	private UzzzCsdnCrawler uzzzCsdnCrawler;

	@GetMapping("csdn_search")
	@ResponseBody
	public String search(String key, //
			@RequestParam(required = false, defaultValue = "1") int start, //
			@RequestParam(required = false, defaultValue = "5") int end) throws IOException {
		uzzzCsdnCrawler.csdn_search(key, start, end);
		return "OK";
	}
}
