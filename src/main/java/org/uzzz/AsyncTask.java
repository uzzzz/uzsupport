package org.uzzz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.uzzz.utils.SecurityUtil;

@Component
public class AsyncTask {

	@Value("${uzzz.path}")
	private String uzzzPath;

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
		return s;
	}

	@Async
	public void postBlog(String title, String c) {
		try {
			int uid = random(2, 24);
			HttpHeaders h = new HttpHeaders();
			// 请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
			h.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			// 封装参数，千万不要替换为Map与HashMap，否则参数无法传递
			MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
			// 也支持中文
			params.add("uid", String.valueOf(uid));
			params.add("title", title);
			params.add("content", c);
			HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(params, h);
			// 执行HTTP请求
			long id = rest.postForObject("https://blog.uzzz.org/api/post", entity, Long.class);

			try { // post baidu
				String postUrl = "http://data.zz.baidu.com/urls?site=https://blog.uzzz.org&token=pJ67TFnK02hkMHlt";
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.TEXT_PLAIN);
				String content = "https://blog.uzzz.org/view/" + id;
				HttpEntity<String> requestEntity = new HttpEntity<String>(content, headers);
				// 执行HTTP请求
				String ret = rest.postForObject(postUrl, requestEntity, String.class);
				System.out.println("post baidu : " + ret + " (" + content + ")");
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeGit(String title, String c, String time) {
		try {
			c = URLEncoder.encode(c, "UTF-8");
			c = "{{ \"" + c + "\" | url_decode}}";
		} catch (UnsupportedEncodingException ee) {
			c = "{% raw %} \n" + c + "\n{% endraw %}";
		}
		try {
			String content = "---\n" //
					+ "layout: default\n" //
					+ "title: " + title + "\n" //
					+ "---\n\n" //
					+ c;

			String path = uzzzPath + "/_posts/";

			String clearTitle = SecurityUtil.signatureByMD5(title).toLowerCase() + ".html";

			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(path + time + "-" + clearTitle, false), "UTF-8"));
			writer.write(content);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String commitAndPushGit() {
		try {
			long start = System.currentTimeMillis();

			File dir = new File(uzzzPath);
			String[] cmd = new String[] { "/bin/sh", "-c",
					"git add . && git commit -a -m \"crawler\" && git pull && git push origin master" };
			Process process = Runtime.getRuntime().exec(cmd, null, dir);

			// 记录dos命令的返回信息
			StringBuffer resStr = new StringBuffer();
			InputStream in = process.getInputStream();
			Reader reader = new InputStreamReader(in);
			BufferedReader bReader = new BufferedReader(reader);
			for (String res = ""; (res = bReader.readLine()) != null;) {
				resStr.append(res + "\n");
			}
			bReader.close();
			reader.close();

			// 记录dos命令的返回错误信息
			StringBuffer errorStr = new StringBuffer();
			InputStream errorIn = process.getErrorStream();
			Reader errorReader = new InputStreamReader(errorIn);
			BufferedReader eReader = new BufferedReader(errorReader);
			for (String res = ""; (res = eReader.readLine()) != null;) {
				errorStr.append(res + "\n");
			}
			eReader.close();
			errorReader.close();

			process.getOutputStream().close(); // 不要忘记了一定要关

			long end = System.currentTimeMillis();

			return "git OK:" + (end - start) + "ms<br />success:" + resStr.toString() + "<br />" + errorStr;
		} catch (IOException ee) {
			return ee.getMessage();
		}
	}

}
