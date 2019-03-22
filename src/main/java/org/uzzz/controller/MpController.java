package org.uzzz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.uzzz.handler.Handler;
import org.uzzz.handler.ProxyData;
import org.uzzz.handler.ProxyHandler;

@Controller
@RequestMapping("mp")
public class MpController {

	@Autowired
	private ProxyHandler proxyHandler;

	@PostMapping
	@ResponseBody
	public String receive(String host, String path, String contentType, String source) {
		String prefix = "https://mp.weixin.qq.com";
		if (path.startsWith(prefix)) {
			path = path.substring(prefix.length());
		}
		ProxyData proxyData = new ProxyData(host, path, contentType, source);

		Handler.executor.execute(proxyHandler.with(proxyData));

		return "OK";
	}
}
