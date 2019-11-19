package org.uzzz.crawlers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
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
import org.uzzz.utils.Utils;

@Component
public class VlanguageCsdnCrawler {

	private static Log log = LogFactory.getLog(VlanguageCsdnCrawler.class);

	@Autowired
	private RefererSlaveDao refererSlaveDao;

	@Autowired
	private RestTemplate rest;

	public long url(String url) throws IOException {
		try {
			Connection _conn = Jsoup.connect(url);
			_conn.header("Cookie",
					"TINGYUN_DATA=%7B%22id%22%3A%22-sf2Cni530g%23HL5wvli0FZI%22%2C%22n%22%3A%22WebAction%2FCI%2Farticle%252Fdetails%22%2C%22tid%22%3A%2211b136d6518e7d2%22%2C%22q%22%3A0%2C%22a%22%3A119%7D; c-login-auto=3; dc_tos=q0wduw; Hm_ct_6bcd52f51e9b3dce32bec4a3997715ac=6525*1*10_6645302180-1573629132712-524858; Hm_lpvt_6bcd52f51e9b3dce32bec4a3997715ac=1573631528; Hm_lvt_6bcd52f51e9b3dce32bec4a3997715ac=1573629133; acw_sc__v2=5dcbb625fca1d73689c038ca693194995ec20fd5; acw_sc__v3=5dcbb6260415413f4012c95c831f9cddb7185987; acw_tc=2760821d15736292261247318e3d56ed02de11ee4b7d215a4b665089fc5fc4; announcement=%257B%2522isLogin%2522%253Afalse%252C%2522announcementUrl%2522%253A%2522https%253A%252F%252Fblogdev.blog.csdn.net%252Farticle%252Fdetails%252F102605809%2522%252C%2522announcementCount%2522%253A0%252C%2522announcementExpire%2522%253A3600000%257D; dc_session_id=10_1573629132712.222920; uuid_tt_dd=10_6645302180-1573629132712-524858");
			Document _doc = _conn.get();
//			log.error(_doc.html());
			String title = _doc.select(".title-article").text();
			String date = _doc.select(".article-bar-top .time").text();
			List<String> categories = _doc.select(".tags-box.space a").eachText();
			List<String> tags = _doc.select(".tags-box.artic-tag-box a").eachText();

			Elements article = _doc.select("article");
			article.select("img").stream().parallel().forEach(element -> {
				imgUrl(element);
			});
			article.select("script, #btn-readmore, .article-copyright").remove();
			String c = article.html();

			// post to vlanguage.cn
			postToVlanguage(title, c, date, categories, tags);
		} catch (IOException ioe) {
		}
		return 0;
	}

	public void csdn_search(String key, int start, int end) throws IOException {
		String _key = key;
		try {
			_key = URLEncoder.encode(key, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error(e);
		}
		String urlTemp = "https://so.csdn.net/so/search/s.do?t=blog&q=%s&p=%d";
		for (int p = start; p <= end; p++) {
			try {
				String url = String.format(urlTemp, _key, p);
				Connection conn = Jsoup.connect(url);
				conn.header("Cookie",
						"acw_tc=2760825515712912527004983ee8247ac502fbea12497869eec4495e7888a0; uuid_tt_dd=10_6645302180-1571291253630-318915; dc_session_id=10_1571291253630.529457; acw_sc__v2=5da800745aa11f99235cde7203d7707328e7c171");
				Document doc = conn.get();

				Elements elements = doc.select("dl.search-list");
				for (Element e : elements) {
					String value = e.attr("data-report-click");
					String article_url = Utils.substring(value, "\"dest\":\"", "\",\"");
					if (article_url != null && article_url.startsWith("https://blog.csdn.net")) {
						url(article_url);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				log.error(e.getMessage(), e);
			}
		}
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

	public void postToVlanguage(//
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
					"https://vlanguage.cn/wp-json/wp/v2/posts", entity, String.class);
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
					"https://vlanguage.cn/wp-json/wp/v2/tags", entity, String.class);
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
					"https://vlanguage.cn/wp-json/wp/v2/categories", entity, String.class);
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
