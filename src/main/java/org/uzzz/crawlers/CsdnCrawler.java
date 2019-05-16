package org.uzzz.crawlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.uzzz.bean.Referer;
import org.uzzz.dao.slave.RefererSlaveDao;
import org.uzzz.tasks.AsyncTask;
import org.uzzz.tasks.GitTask;
import org.uzzz.utils.Utils;

@Component
public class CsdnCrawler {

	private static Log log = LogFactory.getLog(CsdnCrawler.class);

	@Autowired
	private RefererSlaveDao refererSlaveDao;

	@Autowired
	private AsyncTask task;

	@Autowired
	private GitTask gitTask;

	public void crawl_all() {
		task.asyncRun(() -> {
			log.warn("crawl_all");
			try {
				blockchain();
				careerlife();
				ai();
				datacloud();
			} catch (IOException e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
			}
		});
	}

	public void blockchain() throws IOException {
		// 区块链
		String url = "https://www.csdn.net/nav/blockchain";
		crawl(2, url);
	}

	public void careerlife() throws IOException {
		// 程序人生，其他
		String[] urls = new String[] { "https://www.csdn.net/nav/career", //
				"https://www.csdn.net/nav/other" };
		crawl_multiple(3, urls);
	}

	public void ai() throws IOException {
		// 人工智能
		String url = "https://www.csdn.net/nav/ai";
		crawl(4, url);
	}

	public void datacloud() throws IOException {
		// 云计算/大数据
		String url = "https://www.csdn.net/nav/cloud";
		crawl(5, url);
	}

	private void crawl_multiple(int cid, String... urls) throws IOException {
		if (urls != null && urls.length > 0) {
			for (String url : urls) {
				crawl(cid, url);
			}
		}
	}

	private void crawl(int cid, String url) throws IOException {

		Connection conn = Jsoup.connect(url);
		conn.header("Cookie", "uuid_tt_dd=10_6645302180-1555902674734-219524;");
		Document doc = conn.get();

		Elements elements = doc.select("#feedlist_id li[data-type=blog]");

		for (Element e : elements) {
			try {
				Element a = e.select(".title a").first();
				String title = a.text();
				String _url = a.attr("href");
				Document _doc = Jsoup.connect(_url).get();
				String time = _doc.select(".time").first().text().split("日")[0].replace("年", "-").replace("月", "-");

				List<String> thumbnails = new ArrayList<>();

				Elements article = _doc.select("article");
				article.select("img").stream().parallel().forEach(element -> {
					String src = imgUrl(element);
					thumbnails.add(src);
				});
				article.select("script, #btn-readmore, .article-copyright").remove();
				String c = article.html();

				// post uzshare
				long id = task.postBlog(cid, title, c, thumbnails.size() > 0 ? thumbnails.get(0) : "");
				if (id > 0) {
					gitTask.writeGit(id, title, c, time);
				}
			} catch (IOException ioe) {
			}
		}
	}

	public long url(String url) throws IOException {
		return url(url, "");
	}

	public long url(String url, String tags) throws IOException {
		long id = 0;
		try {
			Document _doc = Jsoup.connect(url).get();
			String time = _doc.select(".time").first().text().split("日")[0].replace("年", "-").replace("月", "-");
			String title = _doc.select(".title-article").text();
			Elements article = _doc.select("article");

			List<String> thumbnails = new ArrayList<>();

			article.select("img").stream().parallel().forEach(element -> {
				String src = imgUrl(element);
				thumbnails.add(src);
			});
			article.select("script, #btn-readmore, .article-copyright").remove();
			String c = article.html();

			// post uzshare
			id = task.postBlog(title, c, thumbnails.size() > 0 ? thumbnails.get(0) : "", tags);
			if (id > 0) {
				gitTask.writeGit(id, title, c, time);
			}
		} catch (IOException ioe) {
		}
		return id;
	}

	public void crawl_search(String key, int start, int end) throws IOException {
		task.asyncRun(() -> {
			String urlTemp = "https://so.csdn.net/so/search/s.do?t=blog&q=%s&p=%d";
			for (int p = start; p <= end; p++) {
				try {
					String url = String.format(urlTemp, key, p);
					Connection conn = Jsoup.connect(url);
					conn.header("Cookie", "uuid_tt_dd=10_6645302180-1557902675732-219574;");
					Document doc = conn.get();

					Elements elements = doc.select("dl.search-list");
					for (Element e : elements) {
						String value = e.attr("data-track-click");
						String article_url = Utils.substring(value, "\"con\":\"", "\"}");
						if (article_url != null && article_url.startsWith("https://blog.csdn.net")) {
							url(article_url, key);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					log.error(e.getMessage(), e);
				}
			}
		});
	}

	private String imgUrl(Element element) {

		String src = element.absUrl("src");

		if (StringUtils.isNotBlank(src)) {
			List<Referer> referers = refererSlaveDao.findAll();
			boolean b = false;
			for (Referer r : referers) {
				String host = r.getHost();
				if (src.startsWith("http://" + host) //
						|| src.startsWith("https://" + host)) {
					b = true;
					break;
				}
			}
			if (b) {
				src = "https://uzshare.com/_p?" + src;
				element.attr("src", src);
			}
		}

		return src;
	}

}
