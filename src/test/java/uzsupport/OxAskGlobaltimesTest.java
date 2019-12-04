package uzsupport;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.uzzz.SupportApp;
import org.uzzz.crawlers.OxAskGlobaltimesCrawler;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SupportApp.class)
public class OxAskGlobaltimesTest {

	@Autowired
	private OxAskGlobaltimesCrawler crawler;

	// http://www.globaltimes.cn/opinion/index.html
	@Test
	public void search() throws IOException {
		String[] cates = new String[] { "source", "world", "opinion", "life", "arts" };
		for (String c : cates) {
			String category = c.toUpperCase();
			String toLowerCase = category.toLowerCase();
			for (int i = 1; i <= 10; i++) {
				String listUrl;
				if (i == 1) {
					listUrl = "http://www.globaltimes.cn/" + toLowerCase + "/index.html";
				} else {
					listUrl = "http://www.globaltimes.cn/" + toLowerCase + "/index" + i + ".html";
				}
				crawler.list(listUrl, category);
			}
		}
	}
}
