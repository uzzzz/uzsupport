package uzsupport;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.uzzz.SupportApp;
import org.uzzz.tasks.AsyncTask;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SupportApp.class)
public class RepeatTest {

	@Autowired
	private AsyncTask asyncTask;

	@Test
	public void delete() {
		List<Long> list = new ArrayList<>();
		list.add(677796L);
		list.add(677487L);
		asyncTask.deletePost(list);
	}
}
