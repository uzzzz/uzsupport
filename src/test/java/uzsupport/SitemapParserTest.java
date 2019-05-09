package uzsupport;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.uzzz.SupportApp;

import com.uzshare.sitemapparser.SitemapParser;
import com.uzshare.sitemapparser.SitemapParserCallback;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SupportApp.class)
public class SitemapParserTest {

	@Test
	public void parse() {
		String sitmapindex = "http://www.woshipm.com/sitemap.xml";

//		String sitmapindex = "https://blog.csdn.net/s/sitemap/pcsitemapindex.xml";
		SitemapParser sp = new SitemapParser(new SitemapParserCallback() {

			@Override
			public void sitemap(String sitemap) {
				System.out.println("sitemap:" + sitemap);
			}

			@Override
			public void url(String url) {
				System.out.println("url:" + url);
			}
		});
		sp.parseXml(sitmapindex);
	}
}
