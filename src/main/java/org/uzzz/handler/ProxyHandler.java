package org.uzzz.handler;

import java.util.Date;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.uzzz.bean.WXArticle;
import org.uzzz.bean.WXArticleContent;
import org.uzzz.dao.WXArticleContentRepository;
import org.uzzz.dao.WXArticleRepository;

/**
 * <pre>
 * 历史 第一页 ->
 * host: mp.weixin.qq.com
 * path: /mp/profile_ext?action=home&__biz=MzAwMjExODY5NQ==&scene=124&uin=MTI3MjEyMzE1&key=e3b1ba93cc346df02e83837a5b5b789e4e3b711ac857f6ff7fdd290f1f4b156d9f522e7c8df5e93f14968391af59fe45aad70bba6a5a6b1a99fb3e9f9b38d0f64fde189ec0f75fc1724ba4ca26053ae5&devicetype=iOS10.2&version=16050321&lang=zh_CN&nettype=WIFI&a8scene=3&fontScale=100&pass_ticket=OEOMx7Gmr6rMp4qbF8swmv15gpUvXMGGR%2F0L0lC50XY%3D&wx_header=1
 * contentType: text/html; charset=UTF-8
 * 
 * 历史 第二页及以后 ->
 * host: mp.weixin.qq.com
 * path: /mp/profile_ext?action=getmsg&__biz=MzAwMjExODY5NQ==&f=json&frommsgid=1000010202&count=10&scene=124&is_ok=1&uin=MTI3MjEyMzE1&key=2176f8683275c7bd96e45016551e8ee445dd068bf9bd9736c5c5b2a6fb822c37d965e79707611a570dbc36f13edf26de80d362907063f93e2a4ca9ad29237bd3963e227d9c97adb9adf216437edfa2a4&pass_ticket=OEOMx7Gmr6rMp4qbF8swmv15gpUvXMGGR%2F0L0lC50XY%3D&wxtoken=&x5=0
 * contentType: application/json; charset=UTF-8
 *
 * 文章详情
 * host: mp.weixin.qq.com
 * path: /s?__biz=MzI2NTA0NTI1MA==&mid=2652306983&idx=1&sn=75eef4a2d83618de4d4810cfbbcc753e&chksm=f1410a4ac636835c3fa8a0b35430fe2c5983620cd912cc4365994dbf97ed7e4ede764d2a509a&scene=37&key=564c3e9811aee0abc7136134d15b9913c0b4fcb29c896a7268ae62a3ff3278fa5e11c2de577d356977c33f0f1a8c588fa3038dd23f7b27a53274e26d20bb8e657cae5f147304f98523cf305e0ed7b098&ascene=3&uin=MTI3MjEyMzE1&devicetype=iOS10.2&version=16050222&nettype=WIFI&abtest_cookie=AQABAAgAAQAihh4AAAA%3D&fontScale=100&pass_ticket=OVuPqc0UpAejdyTC%2F4KHOgCyvUixa%2FJdQQOwvg3CJoc%3D&wx_header=1
 * contentType: text/html; charset=UTF-8
 * 
 * </pre>
 */
@Component
public class ProxyHandler extends Handler<ProxyData> {

	private static Logger logger = Logger.getLogger(ProxyHandler.class);

	@Autowired
	private WXArticleRepository wxArticleRepository;

	@Autowired
	private WXArticleContentRepository wxACRepository;

	@Autowired
	private WXArticleHandler wxArticleHandler;

	@Autowired
	private WXArticleContentHandler wxArticleContentHandler;

	@Override
	public void run(ProxyData data) {
		logger.info(data);
		try {
			String path = data.getPath();
			String contentType = data.getContentType();
			if (path.startsWith("/mp/profile_ext?action=home")) {
				// 历史列表 第一页
				handleHistoryList(path, contentType, data.getSource());
			} else if (path.startsWith("/mp/profile_ext?action=getmsg")) {
				// 历史列表 第二页
				handleHistoryList(path, contentType, new JSONObject(data.getSource()));
			} else if (path.startsWith("/s?__biz=")) { // 文章详情
				handleArticle(path, contentType, data.getSource());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// 历史文章第一页
	private void handleHistoryList(String path, String contentType, String source) {

		String biz = null;
		String path_ps = path.substring(path.indexOf("?") + 1);
		String[] path_params = path_ps.split("&");
		for (String param : path_params) {
			param = param.trim();
			if (param.startsWith("__biz")) {
				biz = param.substring(param.indexOf("=") + 1);
				break;
			}
		}
		Document doc = Jsoup.parse(source);

		String msgList = null;
		String scriptString = null;
		for (Element e : doc.body().select("script")) {
			if (e.html().contains("msgList")) {
				scriptString = e.html();
			}
		}

		String split = ";\r";
		if (scriptString.contains(";\r\n")) {
			split = ";\r\n";
		} else if (scriptString.contains(";\n")) {
			split = ";\n";
		}

		String[] scripts = scriptString.split(split);
		for (String s : scripts) {
			s = s.trim();
			if (s.contains("=")) {
				int eq = s.indexOf("=");
				String key = s.substring(0, eq);
				String value = s.substring(eq + 1);
				if (key.contains("msgList")) {
					int begin = value.indexOf("'") + 1;
					int end = value.indexOf("'", begin);
					msgList = value.substring(begin, end).replace("&quot;", "\"");
				}
			}
		}

		try {
			System.out.println(msgList);
			JSONObject json = new JSONObject(msgList);
			JSONArray list = json.getJSONArray("list");

			for (int i = 0; i < list.length(); i++) {
				JSONObject jo = list.getJSONObject(i);

				JSONObject app_msg_ext_info = jo.optJSONObject("app_msg_ext_info");

				if (app_msg_ext_info == null) {
					continue;
				}

				String author = app_msg_ext_info.getString("author");
				String content = app_msg_ext_info.getString("content");
				String content_url = decodeHTML(app_msg_ext_info.getString("content_url"));
				String cover = app_msg_ext_info.getString("cover");
				String digest = app_msg_ext_info.getString("digest");
				String source_url = app_msg_ext_info.getString("source_url");
				String title = app_msg_ext_info.getString("title");
				int is_multi = app_msg_ext_info.getInt("is_multi");

				JSONObject comm_msg_info = jo.getJSONObject("comm_msg_info");
				long datetime = comm_msg_info.getLong("datetime");
				Date date = new Date(datetime * 1000);

				String mid = null;
				int idx = 0;
				String sn = null;
				String ps = content_url.substring(content_url.indexOf("?") + 1);
				String[] params = ps.split("&");

				for (String param : params) {
					param = param.trim();
					if (param.startsWith("mid")) {
						mid = param.substring(param.indexOf("=") + 1);
					} else if (param.startsWith("idx")) {
						idx = Integer.parseInt(param.substring(param.indexOf("=") + 1));
					} else if (param.startsWith("sn")) {
						sn = param.substring(param.indexOf("=") + 1);
					}
				}

				WXArticle wxArticle = wxArticleRepository.findByBizAndMidAndIdx(biz, mid, idx);
				if (wxArticle == null) {
					wxArticle = new WXArticle();
				}
				wxArticle.setBiz(biz);
				wxArticle.setMid(mid);
				wxArticle.setIdx(idx);
				wxArticle.setSn(sn);
				wxArticle.setAuthor(author);
				wxArticle.setContent(content);
				wxArticle.setContentUrl(content_url);
				wxArticle.setCover(cover);
				wxArticle.setDigest(digest);
				wxArticle.setSourceUrl(source_url);
				wxArticle.setTitle(title);
				wxArticle.setMulti(is_multi);
				wxArticle.setDatetime(date);
				wxArticle.setBiz(biz);

				wxArticle = wxArticleRepository.save(wxArticle);

				executor.execute(wxArticleHandler.with(wxArticle));

				if (is_multi == 1) {
					JSONArray multi_app_msg_item_list = app_msg_ext_info.getJSONArray("multi_app_msg_item_list");
					for (int j = 0; j < multi_app_msg_item_list.length(); j++) {
						JSONObject _jo = multi_app_msg_item_list.getJSONObject(j);
						String _author = _jo.getString("author");
						String _content = _jo.getString("content");
						String _content_url = decodeHTML(_jo.getString("content_url"));
						String _cover = _jo.getString("cover");
						String _digest = _jo.getString("digest");
						String _source_url = _jo.getString("source_url");
						String _title = _jo.getString("title");

						String _mid = null;
						int _idx = 0;
						String _sn = null;
						String _ps = _content_url.substring(_content_url.indexOf("?") + 1);
						String[] _params = _ps.split("&");

						for (String _param : _params) {
							_param = _param.trim();
							if (_param.startsWith("mid")) {
								_mid = _param.substring(_param.indexOf("=") + 1);
							} else if (_param.startsWith("idx")) {
								_idx = Integer.parseInt(_param.substring(_param.indexOf("=") + 1));
							} else if (_param.startsWith("sn")) {
								_sn = _param.substring(_param.indexOf("=") + 1);
							}
						}

						WXArticle _wxArticle = wxArticleRepository.findByBizAndMidAndIdx(biz, _mid, _idx);
						if (_wxArticle == null) {
							_wxArticle = new WXArticle();
						}
						_wxArticle.setBiz(biz);
						_wxArticle.setMid(_mid);
						_wxArticle.setIdx(_idx);
						_wxArticle.setSn(_sn);
						_wxArticle.setAuthor(_author);
						_wxArticle.setContent(_content);
						_wxArticle.setContentUrl(_content_url);
						_wxArticle.setCover(_cover);
						_wxArticle.setDigest(_digest);
						_wxArticle.setSourceUrl(_source_url);
						_wxArticle.setTitle(_title);
						_wxArticle.setDatetime(date);
						_wxArticle.setBiz(biz);
						_wxArticle = wxArticleRepository.save(_wxArticle);

						executor.execute(wxArticleHandler.with(_wxArticle));
					}
				}
			}
		} catch (JSONException ee) {
			ee.printStackTrace();
		}

	}

	// 历史文章非第一页
	private void handleHistoryList(String path, String contentType, JSONObject source) {
		try {
			String biz = null;
			String path_ps = path.substring(path.indexOf("?") + 1);
			String[] path_params = path_ps.split("&");
			for (String param : path_params) {
				param = param.trim();
				if (param.startsWith("__biz")) {
					biz = param.substring(param.indexOf("=") + 1);
					break;
				}
			}

			String msgList = source.getString("general_msg_list");

			JSONObject json = new JSONObject(msgList);
			JSONArray list = json.getJSONArray("list");

			for (int i = 0; i < list.length(); i++) {
				JSONObject jo = list.getJSONObject(i);

				JSONObject app_msg_ext_info = jo.optJSONObject("app_msg_ext_info");
				if (app_msg_ext_info == null) {
					continue;
				}

				String author = app_msg_ext_info.getString("author");
				String content = app_msg_ext_info.getString("content");
				String content_url = decodeHTML(app_msg_ext_info.getString("content_url"));
				String cover = app_msg_ext_info.getString("cover");
				String digest = app_msg_ext_info.getString("digest");
				String source_url = app_msg_ext_info.getString("source_url");
				String title = app_msg_ext_info.getString("title");
				int is_multi = app_msg_ext_info.getInt("is_multi");

				JSONObject comm_msg_info = jo.getJSONObject("comm_msg_info");
				long datetime = comm_msg_info.getLong("datetime");
				Date date = new Date(datetime * 1000);

				String mid = null;
				int idx = 0;
				String sn = null;
				String ps = content_url.substring(content_url.indexOf("?") + 1);
				String[] params = ps.split("&");

				for (String param : params) {
					param = param.trim();
					if (param.startsWith("mid")) {
						mid = param.substring(param.indexOf("=") + 1);
					} else if (param.startsWith("idx")) {
						idx = Integer.parseInt(param.substring(param.indexOf("=") + 1));
					} else if (param.startsWith("sn")) {
						sn = param.substring(param.indexOf("=") + 1);
					}
				}

				WXArticle wxArticle = wxArticleRepository.findByBizAndMidAndIdx(biz, mid, idx);
				if (wxArticle == null) {
					wxArticle = new WXArticle();
				}
				wxArticle.setBiz(biz);
				wxArticle.setMid(mid);
				wxArticle.setIdx(idx);
				wxArticle.setSn(sn);
				wxArticle.setAuthor(author);
				wxArticle.setContent(content);
				wxArticle.setContentUrl(content_url);
				wxArticle.setCover(cover);
				wxArticle.setDigest(digest);
				wxArticle.setDescription(digest);
				wxArticle.setSourceUrl(source_url);
				wxArticle.setTitle(title);
				wxArticle.setMulti(is_multi);
				wxArticle.setDatetime(date);
				wxArticle.setBiz(biz);

				wxArticle = wxArticleRepository.save(wxArticle);

				executor.execute(wxArticleHandler.with(wxArticle));

				if (is_multi == 1) {
					JSONArray multi_app_msg_item_list = app_msg_ext_info.getJSONArray("multi_app_msg_item_list");
					for (int j = 0; j < multi_app_msg_item_list.length(); j++) {
						JSONObject _jo = multi_app_msg_item_list.getJSONObject(j);
						String _author = _jo.getString("author");
						String _content = _jo.getString("content");
						String _content_url = decodeHTML(_jo.getString("content_url"));
						String _cover = _jo.getString("cover");
						String _digest = _jo.getString("digest");
						String _source_url = _jo.getString("source_url");
						String _title = _jo.getString("title");

						String _mid = null;
						int _idx = 0;
						String _sn = null;
						String _ps = _content_url.substring(_content_url.indexOf("?") + 1);
						String[] _params = _ps.split("&");

						for (String _param : _params) {
							_param = _param.trim();
							if (_param.startsWith("mid")) {
								_mid = _param.substring(_param.indexOf("=") + 1);
							} else if (_param.startsWith("idx")) {
								_idx = Integer.parseInt(_param.substring(_param.indexOf("=") + 1));
							} else if (_param.startsWith("sn")) {
								_sn = _param.substring(_param.indexOf("=") + 1);
							}
						}

						WXArticle _wxArticle = wxArticleRepository.findByBizAndMidAndIdx(biz, _mid, _idx);
						if (_wxArticle == null) {
							_wxArticle = new WXArticle();
						}

						_wxArticle.setBiz(biz);
						_wxArticle.setMid(_mid);
						_wxArticle.setIdx(_idx);
						_wxArticle.setSn(_sn);
						_wxArticle.setAuthor(_author);
						_wxArticle.setContent(_content);
						_wxArticle.setContentUrl(_content_url);
						_wxArticle.setCover(_cover);
						_wxArticle.setDigest(_digest);
						_wxArticle.setSourceUrl(_source_url);
						_wxArticle.setTitle(_title);
						_wxArticle.setDatetime(date);
						_wxArticle.setBiz(biz);

						_wxArticle = wxArticleRepository.save(_wxArticle);
						executor.execute(wxArticleHandler.with(_wxArticle));
					}
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void handleArticle(String path, String contentType, String source) {

		String content_url = "http://mp.weixin.qq.com" + path;

		String biz = null;
		String mid = null;
		int idx = 0;
		String sn = null;
		String ps = path.substring(path.indexOf("?") + 1);
		String[] params = ps.split("&");
		for (String param : params) {
			param = param.trim();
			if (param.startsWith("__biz")) {
				biz = param.substring(param.indexOf("=") + 1);
			} else if (param.startsWith("mid")) {
				mid = param.substring(param.indexOf("=") + 1);
			} else if (param.startsWith("idx")) {
				idx = Integer.parseInt(param.substring(param.indexOf("=") + 1));
			} else if (param.startsWith("sn")) {
				sn = param.substring(param.indexOf("=") + 1);
			}
		}

		Document doc = Jsoup.parse(source);
		String title = "";
		String description = "";
		long datetime = 0;
		String author = "";

		Elements ems = doc.select("em.rich_media_meta_text");
		if (ems.size() >= 2) {
			author = ems.get(1).html();
		}

		String scriptString = "";
		for (Element e : doc.getElementsByTag("script")) {
			if (e.html().contains("ori_head_img_url")) {
				scriptString = e.html();
				break;
			}
		}

		String split = ";\r";
		if (scriptString.contains(";\r\n")) {
			split = ";\r\n";
		} else if (scriptString.contains(";\n")) {
			split = ";\n";
		}

		String[] scripts = scriptString.split(split);
		for (String s : scripts) {
			s = s.trim();
			if (s.startsWith("var ") && s.contains("=")) {
				int eq = s.indexOf("=");
				String key = s.substring(0, eq).trim();
				String value = s.substring(eq + 1);
				if (key.contains("msg_title")) {
					int begin = value.indexOf("\"") + 1;
					int end = value.indexOf("\"", begin);
					title = value.substring(begin, end);
				} else if (key.contains("msg_desc")) {
					int begin = value.indexOf("\"") + 1;
					int end = value.indexOf("\"", begin);
					description = value.substring(begin, end);
				} else if (key.equals("var ct")) {
					int begin = value.indexOf("\"") + 1;
					int end = value.indexOf("\"", begin);
					String ct = value.substring(begin, end);
					datetime = Long.parseLong(ct) * 1000;
				}
			}
		}

		WXArticle wxArticle = wxArticleRepository.findByBizAndMidAndIdx(biz, mid, idx);
		if (wxArticle == null) {
			wxArticle = new WXArticle();
			wxArticle.setTitle(title);
			wxArticle.setDescription(description);
			wxArticle.setBiz(biz);
			wxArticle.setMid(mid);
			wxArticle.setIdx(idx);
			wxArticle.setSn(sn);
			wxArticle.setAuthor(author);
			wxArticle.setContentUrl(content_url);
			wxArticle.setDatetime(new Date(datetime));
			wxArticle = wxArticleRepository.save(wxArticle);
		}

		WXArticleContent wxArticleContent = wxACRepository.findByArticleAndLevel(wxArticle, 0);
		if (wxArticleContent == null) {
			wxArticleContent = new WXArticleContent();
			wxArticleContent.setArticle(wxArticle);
			wxArticleContent.setTitle(wxArticle.getTitle());
		}
		wxArticleContent.setSource(source);
		wxArticleContent.setUrl(content_url);
		wxArticleContent = wxACRepository.save(wxArticleContent);

		executor.execute(wxArticleContentHandler.with(wxArticleContent));
	}

}
