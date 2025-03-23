use chaseoj_dev;

create table tb_sys_user (

    user_id      bigint      unsigned not null comment '用户Id(主键)',
    user_account varchar(20) not null comment '账号',
    nick_name    varchar(20) comment '昵称',
    password     char(60) not null comment '密码',
    create_by    bigint      unsigned not null comment '创建人',
    create_time  datetime    not null comment '创建时间',
    update_by    bigint      unsigned comment '更新人',
    update_time  datetime comment '更新时间',
    primary key (`user_id`),
    unique key `ids_user_account` (`user_account`)
);

drop table tb_test;

create table `tb_test`(
    test_id bigint not null ,
    title text not null ,
    content text not null ,
    primary key (`test_id`)
) engine = InnoDB default charset = utf8mb4;