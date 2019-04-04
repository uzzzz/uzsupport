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
import org.uzzz.jobs.duplicate.DuplicateJob;
import org.uzzz.jobs.semblance.SemblanceJob;
import org.uzzz.jobs.sort.SortJob;
import org.uzzz.jobs.sort.SortRecord;

@Controller
@RequestMapping("job")
public class JobController {

	@SuppressWarnings("rawtypes")
	@Autowired
	private RedisService redisService;

	@Autowired
	private SortJob sortJob;

	@Autowired
	private DuplicateJob duplicateJob;

	@Autowired
	private SemblanceJob semblanceJob;

	// posts结算分值并写入redis
	@GetMapping("sort")
	@ResponseBody
	public String sort() {
		long a = System.currentTimeMillis();
		boolean success = false;
		try {
			success = sortJob.sort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		long b = System.currentTimeMillis();
		return success + ":" + (b - a) + "ms";
	}

	// 获取posts分值列表
	@SuppressWarnings("unchecked")
	@GetMapping("sort/list")
	@ResponseBody
	public List<SortRecord> list( //
			@RequestParam(required = false, defaultValue = "1") int page,
			@RequestParam(required = false, defaultValue = "20") int pagesize) throws Exception {
		page = (--page) < 0 ? 0 : page;
		int start = page * pagesize;
		int end = start + pagesize - 1;
		ListOperations<String, SortRecord> ops = (ListOperations<String, SortRecord>) redisService.opsForList();
		List<SortRecord> list = ops.range("sorted_posts", start, end);
		return list;
	}

	// 删除重复文章，慎用
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

	// 将所有posts写入hadoop
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
