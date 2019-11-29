package uzsupport;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.uzzz.SupportApp;
import org.uzzz.crawlers.OxNewsGlobaltimesCrawler;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SupportApp.class)
public class OxNewsGlobaltimesTest {

	@Autowired
	private OxNewsGlobaltimesCrawler oxNewsGlobaltimesCrawler;

	// http://www.globaltimes.cn/opinion/index.html
	@Test
	public void search() throws IOException {
		String category = "WORLD";
		String toLowerCase = category.toLowerCase();
		for (int i = 1; i <= 10; i++) {
			String listUrl;
			if (i == 1) {
				listUrl = "http://www.globaltimes.cn/" + toLowerCase + "/index.html";
			} else {
				listUrl = "http://www.globaltimes.cn/" + toLowerCase + "/index" + i + ".html";
			}
			oxNewsGlobaltimesCrawler.list(listUrl, category);
		}
	}
}
