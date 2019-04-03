package uzsupport;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.uzzz.SupportApp;
import org.uzzz.jobs.semblance.SemblanceJob;
import org.uzzz.jobs.semblance.SemblanceRecord;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SupportApp.class)
public class SimilarTest {

	@Autowired
	private SemblanceJob semblanceJob;

	@Test
	public void similar() throws IOException {
		SemblanceRecord sr = semblanceJob.similar(1l);
		System.out.println(sr);
	}
}
