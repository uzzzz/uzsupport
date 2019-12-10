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
public class DayNewsCrawler {

	private static Log log = LogFactory.getLog(DayNewsCrawler.class);

	@Autowired
	private RefererSlaveDao refererSlaveDao;

	@Autowired
	private RestTemplate rest;

	private String host = "daynews.cc";

	public void list(String url, String category, String slug) throws IOException {
		try {
			Connection conn = Jsoup.connect(url);
			Document doc = conn.get();
			Elements elements = doc.select("article");
			for (Element e : elements) {
				String article_url = e.select(".entry-title a").attr("href");
				url(article_url, category, slug);
			}
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage(), e);
		}
	}

	private long url(String url, String category, String slug) throws IOException {
		try {
			Connection _conn = Jsoup.connect(url);
			Document _doc = _conn.get();
			String title = _doc.select(".entry-title").text();
			String date = _doc.select(".my-date").text().replace("年", "-").replace("月", "-").replace("日", "");

			List<String> categories = Arrays.asList(category);
			List<String> tags = Arrays.asList();

			Elements article = _doc.select(".single-content");
			article.select(".favorite-box").remove();
			article.select(".abstract").remove();
			article.select("img").stream().parallel().forEach(element -> {
				imgUrl(element);
			});
			String c = article.html();

			// post to daynews.cc
			postToDayNews(title, c, date, categories, slug, tags);
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

	private void postToDayNews(//
			String title, //
			String c, //
			String date, //
			List<String> categories, //
			String cateSlug, //
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
					categoriyList.add(categoryId(cate, cateSlug));
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

	private int categoryId(String category, String slug) {
		String res = null;
		try {
			HttpHeaders h = new HttpHeaders();
			// 请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
			h.setContentType(MediaType.APPLICATION_JSON);
			h.add("Authorization", "Basic ZmFuZHl2b246MTIzNDU2Nw==");

			WpCategoryTag wpCategory = new WpCategoryTag();
			wpCategory.setName(category);
			wpCategory.setSlug(slug);

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

		String slug;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getSlug() {
			return slug;
		}

		public void setSlug(String slug) {
			this.slug = slug;
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
