package org.uzzz.handler;

import java.io.IOException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.uzzz.bean.WXArticle;
import org.uzzz.bean.WXArticleContent;
import org.uzzz.dao.WXArticleContentRepository;

@Component
public class WXArticleHandler extends Handler<WXArticle> {

	@Autowired
	private WXArticleContentRepository wxACRepository;

	@Value("${weixin.url}")
	private String weixinUrl;

	@Autowired
	private WXArticleContentHandler wxArticleContentHandler;

	@Override
	public void run(WXArticle wxArticle) {
		if (wxArticle == null) {
			return;
		}

		String url = wxArticle.getContentUrl().replaceAll("&amp;", "&");
		if (!url.startsWith("http")) {
			url = weixinUrl + url;
		}

		try {
			Document doc = Jsoup.parse(new URL(url), 5000);
			String source = doc.outerHtml();

			WXArticleContent wxArticleContent = wxACRepository.findByArticleAndLevel(wxArticle, 0);
			if (wxArticleContent == null) {
				wxArticleContent = new WXArticleContent();
				wxArticleContent.setArticle(wxArticle);
				wxArticleContent.setTitle(wxArticle.getTitle());
			}
			wxArticleContent.setSource(source);
			wxArticleContent.setUrl(url);
			wxArticleContent = wxACRepository.save(wxArticleContent);

			executor.execute(wxArticleContentHandler.with(wxArticleContent));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
