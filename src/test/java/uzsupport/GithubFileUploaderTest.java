package uzsupport;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.uzzz.SupportApp;
import org.uzzz.crawlers.GithubFileUploader;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SupportApp.class)
public class GithubFileUploaderTest {

	@Autowired
	private GithubFileUploader githubFileUploader;

	@Test
	public void post() throws IOException {
		String src = "https://static.uzshare.com/theme/default/images/logo.png";
		String ret = githubFileUploader.upload(src);
		System.out.println(ret);
	}
}
