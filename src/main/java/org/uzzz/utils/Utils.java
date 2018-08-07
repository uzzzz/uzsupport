package org.uzzz.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

public class Utils {

	public static String blockchain() throws IOException {

		long start = System.currentTimeMillis();

		String url = "https://www.csdn.net/nav/blockchain";
		Connection conn = Jsoup.connect(url);
		conn.header("Cookie", "uuid_tt_dd=8305037545317967273_20181022;");
		Document doc = conn.get();

		Elements elements = doc.select("#feedlist_id li[data-type=blog]");

		for (Element e : elements) {
			Element a = e.select(".title a").first();
			String title = a.text();
			String _url = a.attr("href");
			Document _doc = Jsoup.connect(_url).get();
			String time = _doc.select(".time").first().text().split("日")[0].replace("年", "-").replace("月", "-");
			String c = _doc.select("article").html();

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

			String path = "E:\\workspace\\uzzzz.github.io\\_posts\\";

			String clearTitle = SecurityUtil.signatureByMD5(title).toLowerCase() + ".html";

			FileWriter writer = new FileWriter(path + time + "-" + clearTitle);
			writer.write(content);
			writer.close();

			String sitemap = "E:\\workspace\\uzzzz.github.io\\sitemap.txt";
			String[] s = time.split("-");
			FileWriter writer2 = new FileWriter(sitemap, true);
			writer2.write("\nhttps://uzzz.org/" + s[0] + "/" + s[1] + "/" + s[2] + "/" + clearTitle);
			writer2.close();

		}

		long end = System.currentTimeMillis();

		String git = git();

		return "crawler OK:" + (end - start) + "ms<br />" + git;
	}

	public static String git() throws IOException {

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

		File dir = new File("E:\\\\workspace\\\\uzzzz.github.io");
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
