package com.ershi.ershiapiclientsdk;

import com.ershi.ershiapiclientsdk.client.ErshiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("ershiapi.client")
@Data
@ComponentScan
public class ErshiAPIClientConfig {
    // 密钥
    private String accessKey;
    private String secretKey;


    @Bean
    public ErshiClient ershiClient() {
        return new ErshiClient(accessKey, secretKey);
    }
}
