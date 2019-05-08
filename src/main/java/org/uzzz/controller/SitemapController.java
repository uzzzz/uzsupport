package org.uzzz.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.uzzz.tasks.AsyncTask;
import org.uzzz.tasks.ScheduledTask;

@Controller
@RequestMapping("sitemap")
@Profile({ "prod" })
public class SitemapController {

	@Autowired
	private AsyncTask asyncTask;

	@Autowired
	private ScheduledTask scheduledTask;

	@GetMapping("rewrite")
	@ResponseBody
	public String rewrite() throws IOException {
		asyncTask.asyncRun(() -> {
			try {
				scheduledTask.rewritesitemapxml();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		return "OK";
	}

}
