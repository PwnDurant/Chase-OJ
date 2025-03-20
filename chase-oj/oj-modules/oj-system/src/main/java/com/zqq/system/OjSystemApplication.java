package com.zqq.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.zqq.**.mapper")
public class OjSystemApplication {
    //你换成你说的 直接加到这里面 我看下

    public static void main(String[] args) {
        SpringApplication.run(OjSystemApplication.class,args);
    }

}
