package org.uzzz.tasks;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.uzzz.crawlers.CsdnCrawler;
import org.uzzz.jobs.sort.SortJob;
import org.uzzz.service.PostService;

import com.redfin.sitemapgenerator.ChangeFreq;
import com.redfin.sitemapgenerator.SitemapIndexGenerator;
import com.redfin.sitemapgenerator.W3CDateFormat;
import com.redfin.sitemapgenerator.WebSitemapGenerator;
import com.redfin.sitemapgenerator.WebSitemapUrl;

@Component
public class ScheduledTask {

	private static Log log = LogFactory.getLog(ScheduledTask.class);

	@Value("${uzshare.static.path}")
	private String uzshareStaticPath;

	@Value("${sitemap.sites}")
	private String[] sitemapSites;

	@Autowired
	private CsdnCrawler crawler;

	@Autowired
	private SortJob jobs;

	@Autowired
	private PostService postService;

	@Scheduled(initialDelay = 1000, fixedDelay = 1000 * 60 * 10)
	public void crawl_blockchain() throws IOException {
		log.warn("crawl blockchain @Scheduled");
		crawler.blockchain();
	}

	@Scheduled(initialDelay = 2000, fixedDelay = 1000 * 60 * 10)
	public void crawl_careerlife() throws IOException {
		log.warn("crawl careerlife @Scheduled");
		crawler.careerlife();
	}

	@Scheduled(initialDelay = 3000, fixedDelay = 1000 * 60 * 10)
	public void crawl_ai() throws IOException {
		log.warn("crawl ai @Scheduled");
		crawler.ai();
	}

	@Scheduled(initialDelay = 100 * 1000, fixedDelay = 1000 * 60 * 60 * 12)
	public void rewritesitemapxml() throws IOException {
		log.warn("rewritesitemapxml start");
		String ok = _rewritesitemapxml(sitemapSites);
		log.warn("rewritesitemapxml end : " + ok);
	}

	private String _rewritesitemapxml(String... hosts) throws IOException {
		List<Long> ids = postService.findAllIds();

		if (hosts == null || hosts.length == 0) {
			return "Error!";
		}
		for (String host : hosts) {
			String baseUrl = "https://" + host;
			String localRoot = uzshareStaticPath + host + "/";
			WebSitemapGenerator wsgGzip = WebSitemapGenerator.builder(baseUrl, new File(localRoot)).gzip(true).build();

			for (Long id : ids) {
				WebSitemapUrl url = new WebSitemapUrl.Options(baseUrl + "/view/" + id).priority(0.9)
						.changeFreq(ChangeFreq.DAILY).build();
				wsgGzip.addUrl(url);
			}

			List<File> viewsGzip = wsgGzip.write();

			// 构造 sitemap_index 生成器
			W3CDateFormat dateFormat = new W3CDateFormat(W3CDateFormat.Pattern.DAY);
			SitemapIndexGenerator sitemapIndexGenerator = new SitemapIndexGenerator.Options(baseUrl,
					new File(localRoot + "/sitemap_index.xml")).autoValidate(true).dateFormat(dateFormat).build();

			viewsGzip.forEach(e -> {
				try { // 组装 sitemap 文件 URL 地址
					String url = baseUrl + "/" + e.getName();
					sitemapIndexGenerator.addUrl(url);
				} catch (MalformedURLException mue) {
					mue.printStackTrace();
				}
			});
			// 生成 sitemap_index 文件
			sitemapIndexGenerator.write();
		}

		return "OK";
	}

	@Scheduled(initialDelay = 10 * 60 * 1000, fixedDelay = 1000 * 60 * 60 * 12)
	public void post_sort() {
		try {
			jobs.sort();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}