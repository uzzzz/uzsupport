package org.uzzz.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.uzzz.bean.Post;
import org.uzzz.dao.slave.PostAttributeSlaveDao;
import org.uzzz.dao.slave.PostSlaveDao;
import org.uzzz.tasks.GitTask;

@Controller
@RequestMapping("git")
public class GitController {

	@Autowired
	private GitTask gitTask;

	@Autowired
	private PostSlaveDao postSlaveDao;

	@Autowired
	private PostAttributeSlaveDao postAttributeSlaveDao;

	@GetMapping("write")
	@ResponseBody
	public String write(long id) throws IOException {
		Post p = postSlaveDao.findOne(id);
		String title = p.getTitle();
		String time = new SimpleDateFormat("yyyy-MM-dd").format(p.getCreated());
		String c = postAttributeSlaveDao.findOne(id).getContent();
		gitTask.writeGit(id, title, c, time);
		gitTask.commitAndPushGit();
		return "OK";
	}
}
