package org.uzzz.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.uzzz.utils.SecurityUtil;

@RestController
@RequestMapping("crawl")
public class CrawlController {

	@GetMapping("blockchain")
	public String blockchain() throws IOException {

		long start = System.currentTimeMillis();

		String url = "https://www.csdn.net/nav/blockchain";
		Connection conn = Jsoup.connect(url);
		conn.header("Cookie", "uuid_tt_dd=8305037566517967273_20181022;");
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

		return "OK:" + (end - start) + "ms";
	}

}
