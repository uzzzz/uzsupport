package uzsupport;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.uzzz.SupportApp;
import org.uzzz.crawlers.VlanguageCsdnCrawler;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SupportApp.class)
public class VlanguageSearchTest {

	@Autowired
	private VlanguageCsdnCrawler vlanguageCsdnCrawler;

	@Test
	public void search() throws IOException {
		String key = "V语言";
		int start = 0;
		int end = 3;
		vlanguageCsdnCrawler.csdn_search(key, start, end);
	}
}
