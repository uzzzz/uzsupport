package org.uzzz;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class SupportApp implements ApplicationContextAware {

	public static ApplicationContext context;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(SupportApp.class, args);
	}

	@SuppressWarnings("unchecked")
	public static <V> RedisService<V> redisService() {
		return context.getBean("redisService", RedisService.class);
	}

	@Autowired
	private RestTemplateBuilder builder;

	@Bean
	public RestTemplate restTemplate() {
		return builder.build();
	}

	@Bean("redisService")
	public <V> RedisService<V> redisService(RedisConnectionFactory factory) {
		RedisService<V> redis = new RedisService<V>(factory);
		redis.afterPropertiesSet();
		return redis;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SupportApp.context = applicationContext;
	}
}
