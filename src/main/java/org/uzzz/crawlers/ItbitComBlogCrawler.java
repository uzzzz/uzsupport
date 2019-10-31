package org.uzzz.crawlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

@Component
public class ItbitComBlogCrawler {

	private static Log log = LogFactory.getLog(ItbitComBlogCrawler.class);

	@Autowired
	private RestTemplate rest;

	public void page(int start, int end) throws IOException {
		String urlTemp = "https://www.itbit.com/blog/page/%d";
		for (int p = start; p <= end; p++) {
			try {
				String url = String.format(urlTemp, p);
				Connection conn = Jsoup.connect(url);
				Document doc = conn.get();

				Elements elements = doc.select(".aa-blog-title a");
				for (Element e : elements) {
					String value = e.attr("href");
					String article_url = value.startsWith("/") ? "https://www.itbit.com" + value : value;
					url(article_url);
				}
			} catch (IOException e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
			}
		}
	}

	public long url(String url) throws IOException {
		try {
			Connection _conn = Jsoup.connect(url);
			Document _doc = _conn.get();
//			log.error(_doc.html());
			String title = _doc.select("#hs_cos_wrapper_name").text();
			String date = null;
			List<String> categories = Arrays.asList("Bitcoin");
			List<String> tags = Arrays.asList("Bitcoin");
			Elements article = _doc.select("#hs_cos_wrapper_post_body");
			String c = article.html();

			// post mlh.app
			postWpMLH(title, c, date, categories, tags);
		} catch (IOException ioe) {
		}
		return 0;
	}

	public void postWpMLH(//
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
					tagList.add(tagId(t));
				}
				p.setTags(tagList);
			}

			HttpEntity<WpPost> entity = new HttpEntity<WpPost>(p, h);
			// 执行HTTP请求
			ResponseEntity<String> res = rest.postForEntity( //
					"http://mlh.app/wp-json/wp/v2/posts", entity, String.class);
			log.info(res.getStatusCode());
			log.info(res.getBody());
		} catch (HttpClientErrorException e) {
			log.error(e.getResponseBodyAsString());
			log.error(e);
			e.printStackTrace();
		}
	}

	public int tagId(String tag) {
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
					"http://mlh.app/wp-json/wp/v2/tags", entity, String.class);
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

	public int categoryId(String category) {
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
					"http://mlh.app/wp-json/wp/v2/categories", entity, String.class);
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
