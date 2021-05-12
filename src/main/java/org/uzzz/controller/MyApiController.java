package org.uzzz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.uzzz.bean.JavaClass;
import org.uzzz.compiler.JavaStringCompiler;
import org.uzzz.dao.JavaClassDao;
import org.uzzz.handler.Handler;
import org.uzzz.handler.ProxyData;
import org.uzzz.handler.ProxyHandler;
import org.uzzz.handler.parser.IParser;

import java.lang.reflect.Method;
import java.util.Map;

@Controller
@RequestMapping("my")
public class MyApiController {

	@Autowired
	private JavaClassDao javaClassDao;

	@GetMapping("api")
	@ResponseBody
	public String api(String name, String method, String[] args) throws Exception{

		JavaClass javaClass = javaClassDao.findByName(name);

		JavaStringCompiler compiler = new JavaStringCompiler();
		Map<String, byte[]> results = compiler.compile(name + ".java", javaClass.getSource());
		Class<?> clazz = compiler.loadClass(name, results);

		if(args == null || args.length == 0) {
			Method m = clazz.getMethod(method);
			String ret = (String) m.invoke(clazz.newInstance());
			return ret;
		}else{
			Method m = clazz.getMethod(method, new Class[]{String[].class});
			String ret = (String) m.invoke(clazz.newInstance(), new Object[]{args});
			return ret;
		}
	}

}
