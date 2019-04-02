package org.uzzz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.uzzz.jobs.duplicate.DuplicateJob;
import org.uzzz.jobs.semblance.SemblanceJob;

@Controller
@RequestMapping("job")
public class JobController {

	@Autowired
	private DuplicateJob duplicateJob;

	@Autowired
	private SemblanceJob semblanceJob;

	@GetMapping("duplicate")
	@ResponseBody
	public String duplicate() {
		long a = System.currentTimeMillis();
		boolean success = false;
		try {
			success = duplicateJob.duplicate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		long b = System.currentTimeMillis();
		return success + ":" + (b - a) + "ms";
	}

	@GetMapping("semblance")
	@ResponseBody
	public String semblance() {
		long a = System.currentTimeMillis();
		boolean success = false;
		try {
			success = semblanceJob.semblance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		long b = System.currentTimeMillis();
		return success + ":" + (b - a) + "ms";
	}
}
