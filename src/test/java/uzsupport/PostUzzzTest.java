package uzsupport;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.uzzz.SupportApp;
import org.uzzz.crawlers.UzzzCsdnCrawler;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SupportApp.class)
public class PostUzzzTest {

	@Autowired
	private UzzzCsdnCrawler uzzzCsdnCrawler;

	@Test
	public void post() throws IOException {
		String key = "";
		int start = 1;
		int end = 5;
		uzzzCsdnCrawler.csdn_search(key, start, end);
	}
}
