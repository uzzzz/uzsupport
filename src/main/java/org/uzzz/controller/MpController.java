package org.uzzz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	public String receive(@RequestBody ProxyData proxyData) {
		String prefix = "https://mp.weixin.qq.com";
		if (proxyData.getPath().startsWith(prefix)) {
			proxyData.setPath(proxyData.getPath().substring(prefix.length()));
		}

		Handler.executor.execute(proxyHandler.with(proxyData));

		return "OK";
	}
}
