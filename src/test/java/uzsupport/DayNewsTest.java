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
			put("entertainment", "娛樂");
			put("emotion", "情感");
			put("international", "國際");
			put("technology", "科技");
			put("sports", "體育");
			put("nba", "NBA");
			put("premier", "英超");
			put("history", "歷史");
			put("game", "遊戲");
			put("health", "養生");
			put("lose", "減肥");
			put("constellation", "星座");
		}
	};

	@Test
	public void search() throws IOException {

		cateslugs.forEach((slug, category) -> {
			int start = 1;
			int end = 30;
			String url = "https://daydaynews.cc/" + slug + "?page=";
			for (int i = start; i <= end; i++) {
				String listUrl = url + i;
				crawler.list(listUrl, category, slug);
			}
		});
	}
}
