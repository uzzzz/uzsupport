package org.uzzz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.uzzz.post.sort.Jobs;

@Controller
@RequestMapping("sort")
public class SortController {

	@GetMapping("start")
	@ResponseBody
	public String start() {
		long a = System.currentTimeMillis();
		try {
			Jobs.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		long b = System.currentTimeMillis();
		return (b - a) + "ms";
	}

}
