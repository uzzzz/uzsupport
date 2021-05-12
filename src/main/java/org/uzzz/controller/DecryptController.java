package org.uzzz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.uzzz.bean.JavaClass;
import org.uzzz.compiler.JavaStringCompiler;
import org.uzzz.dao.JavaClassDao;
import org.uzzz.utils.EncryptUtils;

import java.lang.reflect.Method;
import java.util.Map;

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
