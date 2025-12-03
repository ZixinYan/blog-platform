package com.zixin.blogplatform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.client.HttpClientAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration;

@SpringBootApplication(exclude = {
        HttpClientAutoConfiguration.class,
        RestClientAutoConfiguration.class
})
@MapperScan("com.zixin.blogplatform.dao")
public class BlogPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogPlatformApplication.class, args);
    }

}
