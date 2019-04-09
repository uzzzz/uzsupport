package org.uzzz.tasks;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.uzzz.jobs.semblance.SemblanceJob;
import org.uzzz.service.PostService;

@Component
public class AsyncTask {

	@Autowired
	private SemblanceJob semblanceJob;

	@Autowired
	private PostService postService;

	@Autowired
	private RestTemplate rest;

	@Bean
	public TaskExecutor getTaskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setMaxPoolSize(10);
		taskExecutor.setCorePoolSize(5);
		taskExecutor.setQueueCapacity(20);
		return taskExecutor;
	}

	private int random(int min, int max) {
		int s = (int) min + (int) (Math.random() * (max - min));
		if (s == 88) {
			return random(min, max);
		}
		return s;
	}

	// @Async
	public long postBlog(int cid, String title, String c, String thumbnail) {
//		try {
//			if (semblanceJob.similar(title, c)) {
//				return 0;
//			}
//		} catch (Exception e) {
//		}
		try {
			int uid = random(2, 200);
			HttpHeaders h = new HttpHeaders();
			// 请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
			h.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			// 封装参数，千万不要替换为Map与HashMap，否则参数无法传递
			MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
			// 也支持中文
			params.add("uid", String.valueOf(uid));
			params.add("cid", String.valueOf(cid));
			params.add("title", title);
			params.add("content", c);
			params.add("thumbnail", thumbnail);
			HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(params, h);
			// 执行HTTP请求
			long id = rest.postForObject("https://blog.uzzz.org.cn/api/post", entity, Long.class);
			postBaiduForOrgCn(id);
			return id;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public long syncPostBlog(String title, String c, String thumbnail) {
		return postBlog(1, title, c, thumbnail);
	}

	private void postBaiduForOrgCn(long id) {
		try { // post baidu
			String postUrl = "http://data.zz.baidu.com/urls?site=https://uzzz.org.cn&token=HpJK9usLd5M83kzx";
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.TEXT_PLAIN);
			String content = "https://uzzz.org.cn/view/" + id;
			HttpEntity<String> requestEntity = new HttpEntity<String>(content, headers);
			// 执行HTTP请求
			String ret = rest.postForObject(postUrl, requestEntity, String.class);
			System.out.println("post baidu : " + ret + " (" + content + ")");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Async
	public void deletePost(List<Long> ids) {
		ids.forEach(postService::delete);
	}
}
