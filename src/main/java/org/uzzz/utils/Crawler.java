package org.uzzz.utils;

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
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class Crawler {

	private String Path = "E:\\workspace\\uzzzz.github.io";

	// private static String Path = "E:\\workspace\\uz3.github.io";

	@Autowired
	private RestTemplate rest;

	public String blockchain() throws IOException {

		long start = System.currentTimeMillis();

		String url = "https://www.csdn.net/nav/blockchain";
		Connection conn = Jsoup.connect(url);
		conn.header("Cookie", "uuid_tt_dd=8305037545317967274_20171022;");
		Document doc = conn.get();

		Elements elements = doc.select("#feedlist_id li[data-type=blog]");

		for (Element e : elements) {
			try {
				Element a = e.select(".title a").first();
				String title = a.text();
				String _url = a.attr("href");
				Document _doc = Jsoup.connect(_url).get();
				String time = _doc.select(".time").first().text().split("日")[0].replace("年", "-").replace("月", "-");
				String c = _doc.select("article").html();

				try { // post uzzzblog
					postBlog(title, c);
				} catch (Exception ee) {
					ee.printStackTrace();
				}

				try {
					c = URLEncoder.encode(c, "UTF-8");
					c = "{{ \"" + c + "\" | url_decode}}";
				} catch (UnsupportedEncodingException ee) {
					c = "{% raw %} \n" + c + "\n{% endraw %}";
				}

				String content = "---\n" //
						+ "layout: default\n" //
						+ "title: " + title + "\n" //
						+ "---\n\n" //
						+ c;

				String path = Path + "\\_posts\\";

				String clearTitle = SecurityUtil.signatureByMD5(title).toLowerCase() + ".html";

				BufferedWriter writer = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(path + time + "-" + clearTitle, false), "UTF-8"));
				writer.write(content);
				writer.close();

			} catch (IOException ioe) {
			}
		}

		long end = System.currentTimeMillis();

		String git;
		try {
			git = git();
		} catch (IOException ee) {
			git = ee.getMessage();
		}

		return "crawler OK:" + (end - start) + "ms<br />" + git;
	}

	public String postBlog(String title, String content) {
		HttpHeaders headers = new HttpHeaders();
		// 请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		// 封装参数，千万不要替换为Map与HashMap，否则参数无法传递
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		// 也支持中文
		params.add("title", title);
		params.add("content", content);
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(params,
				headers);
		// 执行HTTP请求
		long id = rest.postForObject("https://blog.uzzz.org/api/post", requestEntity, Long.class);

		// post baidu
		postBaiduForBlog(id);
		return "OK";
	}

	private void postBaiduForBlog(long id) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);
		String content = "https://blog.uzzz.org/view/" + id;
		HttpEntity<String> requestEntity = new HttpEntity<String>(content, headers);
		// 执行HTTP请求
		String ret = rest.postForObject(
				"http://data.zz.baidu.com/urls?site=https://blog.uzzz.org&token=pJ67TFnK02hkMHlt", requestEntity,
				String.class);
		System.out.println("post baidu : " + ret + " (" + content + ")");
	}

	public String git() throws IOException {

		long start = System.currentTimeMillis();

		/**
		 * <pre>
		 * E:\\workspace\\uzzzz.github.io
		 * 
		 * git add .
		 * git commit -m -a "-m-a"
		 * git push master
		 * 
		 * </pre>
		 */

		File dir = new File(Path);
		String[] cmd = new String[] { "cmd", "/c",
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
	}

	public static String getMatcher(String regex, String source) {
		Pattern pattern = Pattern.compile(regex); // 匹配规则
		Matcher matcher = pattern.matcher(source); // 这个是被测试的内容
		return matcher.find() ? matcher.group(1) : "";
	}

	public static <T> T tryGet(Supplier<T> action, T defaultValue) {
		try {
			return action.get();
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static void tryCatch(Runnable action) {
		try {
			action.run();
		} catch (Exception e) {
		}
	}
}
