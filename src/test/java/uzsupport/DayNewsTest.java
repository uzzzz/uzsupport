package uzsupport;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.uzzz.SupportApp;
import org.uzzz.crawlers.DayNewsCrawler;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SupportApp.class)
public class DayNewsTest {

	@Autowired
	private DayNewsCrawler crawler;

	// https://daydaynews.cc/entertainment/

	@Test
	public void search() throws IOException {

		String category = "國際";
		String slug = "international";

		String url = "https://daydaynews.cc/" + slug + "?page=";
		for (int i = 1; i <= 50; i++) {
			String listUrl = url + i;
			crawler.list(listUrl, category, slug);
		}
	}
}
