package org.uzzz.cfg;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories( //
		entityManagerFactoryRef = "masterEntityManagerFactory", //
		transactionManagerRef = "masterTransactionManager", //
		basePackages = { "org.uzzz.dao" }) // 设置Repository所在位置
public class MasterDBConfig {

	@Autowired
	private JpaProperties jpaProperties;

	@Autowired
	@Qualifier("masterDataSource")
	private DataSource masterDataSource;

	@Primary
	@Bean(name = "masterEntityManager")
	public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
		return masterEntityManagerFactory(builder).getObject().createEntityManager();
	}

	@Primary
	@Bean(name = "masterEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean masterEntityManagerFactory(EntityManagerFactoryBuilder builder) {
		return builder.dataSource(masterDataSource).properties(jpaProperties.getHibernateProperties(masterDataSource))
				.packages("org.uzzz.bean") // 设置实体类所在位置
				.persistenceUnit("masterPersistenceUnit").build();
	}

	@Primary
	@Bean(name = "masterTransactionManager")
	public PlatformTransactionManager masterTransactionManager(EntityManagerFactoryBuilder builder) {
		return new JpaTransactionManager(masterEntityManagerFactory(builder).getObject());
	}
}
