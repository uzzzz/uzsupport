package uzsupport;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
	@SuppressWarnings("serial")
	Map<String, String> cateslugs = new HashMap<String, String>() {
		{
			put("sports", "體育");
			put("nba", "NBA");
		}
	};

	@Test
	public void search() throws IOException {

		String slug = "nba";
		int start = 1;
		int end = 7;

		String category = cateslugs.get(slug);
		String url = "https://daydaynews.cc/" + slug + "?page=";
		for (int i = start; i <= end; i++) {
			String listUrl = url + i;
			crawler.list(listUrl, category, slug);
		}
	}
}
