package uzsupport;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.uzzz.SupportApp;
import org.uzzz.post.sort.Jobs;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SupportApp.class)
public class JobTest {

	@Test
	public void testSortJob() {
		long a = System.currentTimeMillis();
		try {
			Jobs.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		long b = System.currentTimeMillis();

		System.out.println(b - a);
	}
}
