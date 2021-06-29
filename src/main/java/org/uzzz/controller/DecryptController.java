package org.uzzz.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.uzzz.utils.EncryptUtils;


@Controller
@RequestMapping("aes")
public class DecryptController {

	@RequestMapping("decrypt")
	@ResponseBody
	public String decrypt(String data) throws Exception{
		String key = "tJjwxDz4WF0Sf9JT";
		String s = EncryptUtils.aesDecrypt(data, key);
		return s;
	}

}
