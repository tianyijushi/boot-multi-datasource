package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "otherEntityManagerFactory",
        transactionManagerRef = "otherTransactionManager",
        basePackages = {"com.example.demo.other.data"}
)
public class otherDataConfig {

    @Autowired
    private JpaProperties jpaProperties;

    @Bean(name = "otherDataSource")
    @ConfigurationProperties(prefix = "other.datasource")
    public DataSource otherDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "otherEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean otherEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("otherDataSource") DataSource otherDataSource) {
        return builder
                .dataSource(otherDataSource)
                .packages("com.example.demo.other.data")
                .properties(jpaProperties.getHibernateProperties(otherDataSource))
                .persistenceUnit("other")
                .build();
    }

    @Bean(name = "otherTransactionManager")
    public PlatformTransactionManager otherTransactionManager(
            @Qualifier("otherEntityManagerFactory") EntityManagerFactory otherEntityManagerFactory) {
        return new JpaTransactionManager(otherEntityManagerFactory);
    }

}