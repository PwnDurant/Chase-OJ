package com.zqq.common.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject,"createTime", LocalDateTime.class,LocalDateTime.now());
        this.strictInsertFill(metaObject,"createBy", Long.class,1902975407502499841L);
    }

//    @Override
//    public void updateFill(MetaObject metaObject) {
//        this.strictUpdateFill(metaObject,"updateTime", LocalDateTime.class,LocalDateTime.now());
//        this.strictUpdateFill(metaObject,"updateBy", Long.class,1902975407502499841L);
//    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("updateBy", 1902975407502499841L, metaObject);
    }
}
