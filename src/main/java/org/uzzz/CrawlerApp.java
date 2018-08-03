package org.uzzz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableWebSecurity
@EnableEurekaClient
@EnableFeignClients
@EnableAsync
public class CrawlerApp extends WebSecurityConfigurerAdapter {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(CrawlerApp.class, args);
	}

	@Value("${eureka.instance.metadata-map.user.name}")
	private String username;

	@Value("${eureka.instance.metadata-map.user.password}")
	private String password;

	@Autowired
	private RestTemplateBuilder builder;

	@Bean
	public RestTemplate restTemplate() {
		return builder.build();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests() //
				.antMatchers("/**").authenticated() //
				.and().csrf().ignoringAntMatchers("/**") //
				.and().httpBasic();
	}

	@Autowired
	public void auth(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser(username).password(password).roles();
	}
}
