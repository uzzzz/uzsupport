package org.uzzz.handler;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.uzzz.bean.Parser;
import org.uzzz.bean.WXArticleContent;
import org.uzzz.dao.ParserRepository;
import org.uzzz.dao.WXArticleContentRepository;
import org.uzzz.handler.parser.IParser.ParserFactory;
import org.uzzz.tasks.AsyncTask;

@Component
public class WXArticleContentHandler extends Handler<WXArticleContent> {

	@Value("${image.proxy}")
	private String imageProxy;

	@Autowired
	private WXArticleContentRepository wxACRepository;

	@Autowired
	private ParserRepository parserRepository;

	@Autowired
	private AsyncTask task;

	/**
	 * 获取Body部分，去掉script，level = 1
	 */
	@Override
	public void run(WXArticleContent wxArticleContent) {
		if (wxArticleContent == null) {
			return;
		}
		WXArticleContent ac = wxACRepository.findByArticleAndLevel(wxArticleContent.getArticle(), 1);
		if (ac == null) {
			ac = new WXArticleContent();
			ac.setLevel(1);
			ac.setArticle(wxArticleContent.getArticle());
			ac.setUrl(wxArticleContent.getUrl());
			ac.setTitle(wxArticleContent.getTitle());
		}

		try {
			String source = wxArticleContent.getSource();

			Document doc = Jsoup.parse(source);
			Element body = doc.body();
			body.select("script").remove();
			String bodySource = body.html();
			ac.setSource(bodySource);
			wxACRepository.save(ac);

			try {
				run2(ac, body);
			} catch (Exception e) {
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 去掉Body中的Title和Comments部分等等, level = 2
	 */
	private void run2(WXArticleContent wxArticleContent, Element body) {
		if (wxArticleContent == null) {
			return;
		}

		WXArticleContent ac = wxACRepository.findByArticleAndLevel(wxArticleContent.getArticle(), 2);
		if (ac == null) {
			ac = new WXArticleContent();
			ac.setLevel(2);
			ac.setArticle(wxArticleContent.getArticle());
			ac.setUrl(wxArticleContent.getUrl());
			ac.setTitle(wxArticleContent.getTitle());
		}

		body.select("#js_top_ad_area, " //
				+ "#js_pc_qr_code, " //
				+ ".rich_media_title, " //
				+ ".rich_media_meta_list, " //
				+ ".rich_media_tool, " //
				+ ".rich_media_area_extra, " //
				+ ".rich_media_area_title, " //
				+ ".relate_article_list, " //
				+ ".sougou").remove();

		ac.setSource(body.html());
		wxACRepository.save(ac);

		try {
			run3(ac, body);
		} catch (Exception e) {
		}

	}

	/**
	 * 处理img标签 level = 3
	 */
	private void run3(WXArticleContent wxArticleContent, Element body) {
		if (wxArticleContent == null) {
			return;
		}

		WXArticleContent ac = wxACRepository.findByArticleAndLevel(wxArticleContent.getArticle(), 3);
		boolean send2blog = false;
		if (ac == null) {
			send2blog = true;
			ac = new WXArticleContent();
			ac.setLevel(3);
			ac.setArticle(wxArticleContent.getArticle());
			ac.setUrl(wxArticleContent.getUrl());
			ac.setTitle(wxArticleContent.getTitle());
		}

		Element content = body;

		Elements es = body.select("#js_content");
		if (es.size() == 1) {
			content = es.get(0);
		}

		List<String> thumbnails = new ArrayList<>();

		Elements imgs = content.getElementsByTag("img");
		for (Element e : imgs) {
			String src = e.attr("src");
			String data_src = e.attr("data-src");
			data_src = data_src.replace("/0?", "/640?");
			if ((!StringUtils.hasText(src) || !src.startsWith("http")) && StringUtils.hasText(data_src)) {
				data_src = imageProxy + data_src;
				e.attr("src", data_src);
				thumbnails.add(data_src);
			}
		}

		content.select("pre.prettyprint").tagName("div");

		String source = null;
		try {
			String biz = ac.getArticle().getBiz();
			Parser p = parserRepository.findByBiz(biz);
			Element s = ParserFactory.get(p).parse(content);
			source = s.html();
		} catch (Exception e) {
			e.printStackTrace();
			source = content.html();
		}

		ac.setSource(source);
		wxACRepository.save(ac);

		try {
			if (send2blog || true /* 暂时忽略send2blog的值 */ ) { // post uzzzblog
				String thumb = "";
				if (thumbnails.size() > 0) {
					int ind = thumbnails.size() / 2;
					thumb = thumbnails.get(ind);
				}
				task.postBlog(ac.getTitle(), ac.getSource(), thumb);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
