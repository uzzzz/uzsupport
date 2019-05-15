package org.uzzz.crawlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.uzzz.bean.Referer;
import org.uzzz.dao.slave.RefererSlaveDao;
import org.uzzz.tasks.AsyncTask;

@Component
public class OschinaCrawler {

	@Autowired
	private RefererSlaveDao refererSlaveDao;

	@Autowired
	private AsyncTask task;

	public void blockchain() throws IOException {
		// 区块链 页数：1 ~ 63
		String template = "https://www.oschina.net/blog/widgets/_blog_index_newest_list?classification=5765988&p=%d&type=ajax";
		for (int i = 1; i <= 63; i++) {
			String url = String.format(template, i);
			Document _doc = Jsoup.connect(url).get();
			_doc.select("a.header").parallelStream().forEach(a -> {
				String _url = a.attr("href");
				url(2, _url);
			});
		}
	}

	public long url(int cid, String url) {
		long id = 0;
		try {
			Document _doc = Jsoup.connect(url).get();
			Elements titleE = _doc.select("h2.header");
			titleE.select("div.label").remove();
			String title = titleE.text();
			Elements article = _doc.select("#articleContent");
			String tags = _doc.select("div.tags a").stream().map(e -> e.text()).collect(Collectors.joining(","));
			List<String> thumbnails = new ArrayList<>();
			article.select("img").stream().parallel().forEach(element -> {
				String src = imgUrl(element);
				thumbnails.add(src);
			});
			article.select("script, div.ad-wrap").remove();
			String c = article.html();
			// post uzshare
			id = task.postBlog(cid, title, c, thumbnails.size() > 0 ? thumbnails.get(0) : "", tags);
		} catch (IOException ioe) {
		}
		return id;
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
