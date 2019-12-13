package org.uzzz.crawlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.uzzz.bean.Referer;
import org.uzzz.dao.slave.RefererSlaveDao;

@Component
public class OxNewsGlobaltimesCrawler {

	private static Log log = LogFactory.getLog(OxNewsGlobaltimesCrawler.class);

	@Autowired
	private RefererSlaveDao refererSlaveDao;

	@Autowired
	private RestTemplate rest;

	private String host = "oxnews.net";

	public void list(String url, String category) throws IOException {
		try {
			Connection conn = Jsoup.connect(url);
			Document doc = conn.get();
			Elements elements = doc.select("#channel-list .row-content");
			for (Element e : elements) {
				String article_url = e.select(".span10 a").attr("href");
				url(article_url, category);
			}
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	private long url(String url, String category) throws IOException {
		try {
			Connection _conn = Jsoup.connect(url);
			Document _doc = _conn.get();
			String title = _doc.select(".article-title").text();
			// text_left = Source:Global Times Published: 2019/11/28 21:23:40
			String text_left = _doc.select(".article-source .text-left").text();
			String date = text_left.substring(text_left.indexOf("Published:") + 10).trim();
			String[] ds = date.split(" ");
			String d = ds[0];
			String t = ds[1];
			{ // 修正日期
				date = "";
				for (String s : d.split("/")) {
					if (s.length() == 4) {
						date = s;
					} else if (s.length() == 1) {
						date += ("-0" + s);
					} else {
						date += ("-" + s);
					}
				}
				date += " ";
			}
			{ // 修正时间
				String[] tt = t.split(":");
				for (int i = 0; i < tt.length; i++) {
					if (tt[i].length() == 1) {
						tt[i] = "0" + tt[i];
					}
					if (i == 0) {
						date += tt[i];
					} else {
						date += ":" + tt[i];
					}
				}
			}

			List<String> categories = Arrays.asList(category);
			List<String> tags = Arrays.asList(_doc.select(".text-muted").text().replace("Posted in:", "").split(","));

			Elements article = _doc.select("#left .span12.row-content");
			article.select(".pull-left").remove();
			article.select("img").stream().parallel().forEach(element -> {
				imgUrl(element);
			});
			String c = article.html();

			// post to oxnews.net
			postToOxNews(title, c, date, categories, tags);
		} catch (IOException ioe) {
		}
		return 0;
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

	private void postToOxNews(//
			String title, //
			String c, //
			String date, //
			List<String> categories, //
			List<String> tags) {
		try {
			HttpHeaders h = new HttpHeaders();
			// 请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
			h.setContentType(MediaType.APPLICATION_JSON);
			h.add("Authorization", "Basic ZmFuZHl2b246MTIzNDU2Nw==");

			WpPost p = new WpPost();
			p.setTitle(title);
			p.setContent(c);
			p.setStatus("publish");
			p.setDate(date);

			if (categories != null) {
				List<Integer> categoriyList = new ArrayList<Integer>();
				for (String cate : categories) {
					categoriyList.add(categoryId(cate));
				}
				p.setCategories(categoriyList);
			}

			if (tags != null) {
				List<Integer> tagList = new ArrayList<Integer>();
				for (String t : tags) {
					try {
						tagList.add(tagId(t));
					} catch (Exception e) {
					}
				}
				p.setTags(tagList);
			}

			HttpEntity<WpPost> entity = new HttpEntity<WpPost>(p, h);
			// 执行HTTP请求
			ResponseEntity<String> res = rest.postForEntity( //
					"https://" + host + "/wp-json/wp/v2/posts", entity, String.class);
			log.info(res.getStatusCode());
			log.info(res.getBody());
		} catch (HttpClientErrorException e) {
			log.error(e.getResponseBodyAsString());
//			log.error("date:" + date);
			log.error(e);
			e.printStackTrace();
		}
	}

	private int tagId(String tag) {
		String res = null;
		try {
			HttpHeaders h = new HttpHeaders();
			// 请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
			h.setContentType(MediaType.APPLICATION_JSON);
			h.add("Authorization", "Basic ZmFuZHl2b246MTIzNDU2Nw==");

			WpCategoryTag wpTag = new WpCategoryTag();
			wpTag.setName(tag);

			HttpEntity<WpCategoryTag> entity = new HttpEntity<WpCategoryTag>(wpTag, h);
			// 执行HTTP请求
			res = rest.postForObject( //
					"https://" + host + "/wp-json/wp/v2/tags", entity, String.class);
			return new JSONObject(res).optInt("id");
		} catch (HttpClientErrorException e) {
			res = e.getResponseBodyAsString();
			try {
				return new JSONObject(res).getJSONObject("data").optInt("term_id");
			} catch (JSONException ee) {
				ee.printStackTrace();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private int categoryId(String category) {
		String res = null;
		try {
			HttpHeaders h = new HttpHeaders();
			// 请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
			h.setContentType(MediaType.APPLICATION_JSON);
			h.add("Authorization", "Basic ZmFuZHl2b246MTIzNDU2Nw==");

			WpCategoryTag wpCategory = new WpCategoryTag();
			wpCategory.setName(category);

			HttpEntity<WpCategoryTag> entity = new HttpEntity<WpCategoryTag>(wpCategory, h);
			// 执行HTTP请求
			res = rest.postForObject( //
					"https://" + host + "/wp-json/wp/v2/categories", entity, String.class);
			return new JSONObject(res).optInt("id");
		} catch (HttpClientErrorException e) {
			res = e.getResponseBodyAsString();
			try {
				return new JSONObject(res).getJSONObject("data").optInt("term_id");
			} catch (JSONException ee) {
				ee.printStackTrace();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return 0;
	}

	class WpCategoryTag {
		String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	class WpPost {

		private String title;
		private String content;
		private String status;
		private String date;
		private List<Integer> categories; //
		private List<Integer> tags;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public List<Integer> getCategories() {
			return categories;
		}

		public void setCategories(List<Integer> categories) {
			this.categories = categories;
		}

		public List<Integer> getTags() {
			return tags;
		}

		public void setTags(List<Integer> tags) {
			this.tags = tags;
		}
	}
}
