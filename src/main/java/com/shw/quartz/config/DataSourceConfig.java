package com.shw.quartz.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author shw
 * @version 1.0
 * @date 2020/9/16 14:17
 * @description
 */
@Configuration
public class DataSourceConfig {

    @ConfigurationProperties(prefix = "spring.datasource.druid")
    @Bean
    public DataSource druidDataSource(){
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        dataSource.setMaxActive(20);
        return dataSource;
    }

}
