package org.uzzz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.uzzz.SimHash;
import org.uzzz.bean.Post;
import org.uzzz.bean.PostAttribute;
import org.uzzz.service.PostService;

@Controller
@RequestMapping("distance")
public class DistanceController {

	@Autowired
	private PostService postService;

	@GetMapping("compare")
	@ResponseBody
	public String compare(long id1, long id2) {
		Post post1 = postService.getPost(id1);
		PostAttribute postAttribute1 = postService.getPostAttribute(id1);
		String title1 = post1.getTitle();
		String content1 = postAttribute1.getContent();

		Post post2 = postService.getPost(id2);
		PostAttribute postAttribute2 = postService.getPostAttribute(id2);
		String title2 = post2.getTitle();
		String content2 = postAttribute2.getContent();

		long a = System.currentTimeMillis();

		SimHash title1hash = new SimHash(title1, 64);
		SimHash title2hash = new SimHash(title2, 64);
		SimHash content1hash = new SimHash(content1, 64);
		SimHash content2hash = new SimHash(content2, 64);

		int titleHamming = title1hash.hammingDistance(title2hash);
		double titleSemblance = title1hash.getSemblance(title2hash);
		int contentHamming = content1hash.hammingDistance(content2hash);
		double contentSemblance = content1hash.getSemblance(content2hash);

		long b = System.currentTimeMillis();
		StringBuffer sb = new StringBuffer("<p>");
		sb.append("耗时:").append(b - a).append("ms<br />") //
				.append("id1:").append(title1).append("<br />") //
				.append("id2:").append(title2).append("<br />") //
				.append("标题-海明距离是:").append(titleHamming).append("<br />") //
				.append("标题-文本相似度:").append(titleSemblance).append("<br />")//
				.append("内容-海明距离是:").append(contentHamming).append("<br />") //
				.append("内容-文本相似度:").append(contentSemblance).append("<br />")//
				.append("</p>");
		return sb.toString();
	}
}
