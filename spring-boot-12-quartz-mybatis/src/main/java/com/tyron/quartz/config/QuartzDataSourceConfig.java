package com.tyron.quartz.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * @Desription quartz定时任务数据源配置
 * @Author: tyron
 * @Date: Created in 2019-2-21
 */
@Configuration
@MapperScan(basePackages = QuartzDataSourceConfig.PACKAGE, sqlSessionFactoryRef = "QuartzDataSourceConfig")
public class QuartzDataSourceConfig {

    static final String PACKAGE = "com.tyron.quartz.dao";
    static final String MAPPER_LOCATION = "classpath:mapper/*.xml";

    @Bean(name = "quartzDataSource")
    @ConfigurationProperties("quartz.datasource")
    public DataSource quartzDataSource() {
        return new DruidDataSource();
    }

    @Bean(name = "quartzTransactionManager")
    public DataSourceTransactionManager quartzTransactionManager() {
        return new DataSourceTransactionManager(quartzDataSource());
    }

    @Bean(name = "quartzSqlSessionFactory")
    public SqlSessionFactory quartzSqlSessionFactory(@Qualifier("quartzDataSource") DataSource quartzDataSource)
            throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(quartzDataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources(QuartzDataSourceConfig.MAPPER_LOCATION));
        return sessionFactory.getObject();
    }

}
