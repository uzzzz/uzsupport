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

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Crawler {

	@Value("${uzzz.path}")
	private String uzzzPath;

	@Autowired
	private AsyncTask task;

	public String blockchain() throws IOException {

		long start = System.currentTimeMillis();

		String url = "https://www.csdn.net/nav/blockchain";
		Connection conn = Jsoup.connect(url);
		conn.header("Cookie", "uuid_tt_dd=83050375453151967274_20161022;");
		Document doc = conn.get();

		Elements elements = doc.select("#feedlist_id li[data-type=blog]");

		for (Element e : elements) {
			try {
				Element a = e.select(".title a").first();
				String title = a.text();
				String _url = a.attr("href");
				Document _doc = Jsoup.connect(_url).get();
				String time = _doc.select(".time").first().text().split("日")[0].replace("年", "-").replace("月", "-");

				Elements article = _doc.select("article");
				article.select("img").stream().parallel().forEach(element -> {
					String src = element.absUrl("src");
					if (src != null && src.startsWith("https://img-blog.csdn.net")) {
						element.attr("src", "https://blog.uzzz.org/_p?" + src);
					}
				});
				String c = article.html();

				// post uzzzblog
				task.postBlog(title, c);

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

				String path = uzzzPath + "/_posts/";

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
	}

}
