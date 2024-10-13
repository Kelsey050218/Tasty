package com.cs183.tasty.entity.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value ="admin")
public class Admin implements Serializable {

    @TableId(value = "admin_id", type = IdType.AUTO)
    private Long adminId;

    //姓名
    @TableField(value = "admin_name")
    private String adminName;

    //密码
    @TableField(value = "password")
    private String password;

    //手机号
    @TableField(value = "phone")
    private String phone;

    //注册时间
    @TableField(value = "create_time")
    private LocalDateTime createTime;

}
