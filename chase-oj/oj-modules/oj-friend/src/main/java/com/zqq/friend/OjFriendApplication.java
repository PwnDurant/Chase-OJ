package com.zqq.friend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@SpringBootApplication
@MapperScan("com.zqq.**.mapper")
@EnableFeignClients(basePackages = "com.zqq.api")
public class OjFriendApplication {
    public static void main(String[] args) {
        SpringApplication.run(OjFriendApplication.class,args);
    }
}
