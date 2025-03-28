package com.zqq.friend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@SpringBootApplication
@MapperScan("com.zqq.**.mapper")
public class OjFriendApplication {
    public static void main(String[] args) {
        SpringApplication.run(OjFriendApplication.class,args);
    }
}
