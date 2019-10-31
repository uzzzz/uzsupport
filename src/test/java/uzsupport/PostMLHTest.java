package uzsupport;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.uzzz.SupportApp;
import org.uzzz.crawlers.ItbitComBlogCrawler;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SupportApp.class)
public class PostMLHTest {

	@Autowired
	private ItbitComBlogCrawler itbitComBlogCrawler;

	@Test
	public void post() {
		try {
			itbitComBlogCrawler.page(1, 16);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
