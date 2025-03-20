package com.zqq.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zqq.common.core.domain.BaseEntity;
import lombok.Data;



@Data
@TableName("tb_sys_user")
public class SysUser extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID) //通过雪花算法生成的主键Id
    private Long userId; //主键，不再使用auto_increment维护主键

    private String userAccount;

    private String password;


}
