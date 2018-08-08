package org.uzzz;

import java.io.IOException;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.uzzz.utils.Utils;

@Component
public class ScheduledTask {

	@Scheduled(initialDelay = 2000, fixedDelay = 1000 * 60 * 10)
	public void crawl() throws IOException {
		System.out.println("crawl @Scheduled");
		Utils.blockchain();
	}
}