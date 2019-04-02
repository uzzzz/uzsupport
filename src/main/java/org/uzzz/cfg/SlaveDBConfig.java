package org.uzzz.cfg;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories( //
		entityManagerFactoryRef = "slaveEntityManagerFactory", //
		transactionManagerRef = "slaveTransactionManager", //
		basePackages = { "org.uzzz.dao.slave" }) // 设置Repository所在位置
public class SlaveDBConfig {

	@Autowired
	private JpaProperties jpaProperties;

	@Autowired
	@Qualifier("slaveDataSource")
	private DataSource slaveDataSource;

	@Bean(name = "slaveEntityManager")
	public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
		return slaveEntityManagerFactory(builder).getObject().createEntityManager();
	}

	@Bean(name = "slaveEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean slaveEntityManagerFactory(EntityManagerFactoryBuilder builder) {
		return builder.dataSource(slaveDataSource).properties(jpaProperties.getHibernateProperties(slaveDataSource))
				.packages("org.uzzz.bean") // 设置实体类所在位置
				.persistenceUnit("slavePersistenceUnit").build();
	}

	@Bean(name = "slaveTransactionManager")
	public PlatformTransactionManager slaveTransactionManager(EntityManagerFactoryBuilder builder) {
		return new JpaTransactionManager(slaveEntityManagerFactory(builder).getObject());
	}
}
