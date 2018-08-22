package org.uzzz.utils;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.uzzz.AsyncTask;

@Component
public class CsdnCrawler {

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
				// write to uzzz
				task.writeGit(title, c, time);
			} catch (IOException ioe) {
			}
		}
		long end = System.currentTimeMillis();

		String git = task.commitAndPushGit();

		return "crawler OK:" + (end - start) + "ms<br />" + git;
	}

}
