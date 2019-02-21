package org.uzzz.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.uzzz.RedisService;
import org.uzzz.post.sort.Jobs;
import org.uzzz.post.sort.PostRecord;

@Controller
@RequestMapping("sort")
public class SortController {

	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisService redisService;

	@Autowired
	private Jobs jobs;

	@GetMapping("start")
	@ResponseBody
	public String start() {
		long a = System.currentTimeMillis();
		boolean success = false;
		try {
			success = jobs.sort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		long b = System.currentTimeMillis();
		return success + ":" + (b - a) + "ms";
	}

	@SuppressWarnings("unchecked")
	@GetMapping("/list")
	@ResponseBody
	public List<PostRecord> list( //
			@RequestParam(required = false, defaultValue = "1") int page,
			@RequestParam(required = false, defaultValue = "20") int pagesize) throws Exception {
		page = (--page) < 0 ? 0 : page;
		int start = page * pagesize;
		int end = start + pagesize - 1;
		ListOperations<String, PostRecord> ops = (ListOperations<String, PostRecord>) redisService.opsForList();
		List<PostRecord> list = ops.range("sorted_posts", start, end);
		return list;
	}
}
