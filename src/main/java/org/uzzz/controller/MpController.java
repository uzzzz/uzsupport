package org.uzzz.controller;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.uzzz.bean.MpSource;
import org.uzzz.dao.MpSourceDao;

@Controller
@RequestMapping("mp")
public class MpController {

	@Autowired
	private MpSourceDao mpSourceDao;

	@PostMapping
	@ResponseBody
	public String receive(String host, String path, String contentType, String source) {
		if (StringUtils.isNotBlank(path) //
				&& path.startsWith("https://mp.weixin.qq.com/s?")) {
			MpSource mpSource = new MpSource();
			mpSource.setContentType(contentType);
			mpSource.setHost(host);
			mpSource.setPath(path);
			mpSource.setSource(source);
			mpSourceDao.save(mpSource);
		}
		return "OK";
	}

}
