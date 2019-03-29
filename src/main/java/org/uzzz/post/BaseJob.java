package org.uzzz.post;

import org.springframework.beans.factory.annotation.Value;

import lombok.Data;

@Data
public class BaseJob {

	@Value("${spring.datasource.driver-class-name}")
	protected String dbdriver;

	@Value("${spring.datasource.url}")
	protected String dburl;

	@Value("${spring.datasource.username}")
	protected String dbuser;

	@Value("${spring.datasource.password}")
	protected String dbpass;

}
