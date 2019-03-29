package org.uzzz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.uzzz.post.duplicate.DuplicateJob;

@Controller
@RequestMapping("duplicate")
public class DuplicateController {

	@Autowired
	private DuplicateJob job;

	@GetMapping("start")
	@ResponseBody
	public String start() {
		long a = System.currentTimeMillis();
		boolean success = false;
		try {
			success = job.duplicate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		long b = System.currentTimeMillis();
		return success + ":" + (b - a) + "ms";
	}
}
