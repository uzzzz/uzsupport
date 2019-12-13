package uzsupport;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.uzzz.SupportApp;
import org.uzzz.crawlers.OxNewsGlobaltimesCrawler;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SupportApp.class)
public class OxNewsGlobaltimesTest {

	@Autowired
	private OxNewsGlobaltimesCrawler oxNewsGlobaltimesCrawler;

	// http://www.globaltimes.cn/opinion/index.html
	@SuppressWarnings("serial")
	Map<String, String> cateslugs = new HashMap<String, String>() {
		{
//			put("CHINA", "china");
//			put("Politics", "china/politics");
//			put("Society", "china/society");
//			put("Diplomacy", "china/diplomacy");
//			put("Military", "china/military");
//			put("Law", "china/law");
//			put("Environment", "china/environment");
//			put("Profile", "china/profile");
//			put("In-Depth", "china/indepth");
//			put("China Watch", "china/china-watch");
//			put("HK/Macao/Taiwan", "china/HK-Macao-Taiwan");
			// -----------
//			put("SOURCE", "source");
//			put("GT Voice", "source/GT-Voice");
//			put("Insight", "source/insight");
//			put("Economy", "source/economy");
//			put("China-US Focus", "source/ChinaUSFocus");
//			put("Comment", "source/comments");
//			put("Just Tech", "source/just-tech");
//			put("Companies", "source/companies");
//			put("Industry", "source/industries");
//			put("Aviation", "source/Aviation");
		}
	};

	@Test
	public void search() throws IOException {

		for (Entry<String, String> set : cateslugs.entrySet()) {
			String category = set.getKey();
			String slug = set.getValue();
			for (int i = 1; i <= 10; i++) {
				String listUrl;
				if (i == 1) {
					listUrl = "http://www.globaltimes.cn/" + slug + "/index.html";
				} else {
					listUrl = "http://www.globaltimes.cn/" + slug + "/index" + i + ".html";
				}
				try {
					oxNewsGlobaltimesCrawler.list(listUrl, category);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
