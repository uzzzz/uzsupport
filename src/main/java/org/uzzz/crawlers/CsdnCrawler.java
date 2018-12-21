package org.uzzz.crawlers;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.uzzz.tasks.AsyncTask;
import org.uzzz.tasks.GitTask;

@Component
public class CsdnCrawler {

	@Autowired
	private AsyncTask task;

	@Autowired
	private GitTask gitTask;

	public String blockchain() throws IOException {
		String url = "https://www.csdn.net/nav/blockchain";
		return crawl(2, url);
	}

	public String careerlife() throws IOException {
		String url = "https://www.csdn.net/nav/career";
		return crawl(3, url);
	}

	private String crawl(int cid, String url) throws IOException {

		long start = System.currentTimeMillis();

		Connection conn = Jsoup.connect(url);
		conn.header("Cookie", "uuid_tt_dd=83050375453476967274_20181022;");
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
				article.select("script, #btn-readmore").remove();
				String c = article.html();

				// post uzzzblog
				task.postBlog(cid, title, c);

				gitTask.writeGit(title, c, time);
			} catch (IOException ioe) {
			}
		}
		long end = System.currentTimeMillis();

		String git = gitTask.commitAndPushGit();

		return "crawler OK:" + (end - start) + "ms<br />" + git;
	}

	public String url(String url) throws IOException {
		String redirect = null;
		try {
			Document _doc = Jsoup.connect(url).get();
			String time = _doc.select(".time").first().text().split("日")[0].replace("年", "-").replace("月", "-");
			String title = _doc.select(".title-article").text();
			Elements article = _doc.select("article");
			article.select("img").stream().parallel().forEach(element -> {
				String src = element.absUrl("src");
				if (src != null && src.startsWith("https://img-blog.csdn.net")) {
					element.attr("src", "https://blog.uzzz.org/_p?" + src);
				}
			});
			article.select("script, #btn-readmore").remove();
			String c = article.html();

			// post uzzzblog
			redirect = task.syncPostBlog(title, c);

			gitTask.writeGit(title, c, time);
			gitTask.commitAndPushGit();
		} catch (IOException ioe) {
		}

		return redirect;
	}

}
