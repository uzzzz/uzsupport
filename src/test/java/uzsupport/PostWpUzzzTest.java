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
public class PostWpUzzzTest {

	@Autowired
	private UzzzCsdnCrawler uzzzCsdnCrawler;

	@Test
	public void post() {
		try {
			uzzzCsdnCrawler.url("https://blog.csdn.net/Applying/article/details/80575616");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
