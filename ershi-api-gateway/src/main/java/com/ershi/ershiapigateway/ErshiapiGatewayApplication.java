package com.ershi.ershiapigateway;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDubbo
@EnableScheduling
public class ErshiapiGatewayApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ErshiapiGatewayApplication.class, args);
    }
}
