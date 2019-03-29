package uzsupport;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.uzzz.SupportApp;
import org.uzzz.service.PostService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SupportApp.class)
public class RepeatTest {

	@Autowired
	private PostService postService;

	@Test
	public void repeat() {
		List<String> list = postService.findAllTitles();
		System.out.println("total:" + list.size());

		Set<String> set = list.stream().collect(Collectors.toSet());
		System.out.println("count:" + set.size());
	}
}
