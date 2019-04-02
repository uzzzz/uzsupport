package org.uzzz.jobs;

import org.springframework.beans.factory.annotation.Value;

import lombok.Data;

@Data
public class BaseJob {

	@Value("${spring.datasource.slave.driver-class-name}")
	protected String dbdriver;

	@Value("${spring.datasource.slave.url}")
	protected String dburl;

	@Value("${spring.datasource.slave.username}")
	protected String dbuser;

	@Value("${spring.datasource.slave.password}")
	protected String dbpass;

}
