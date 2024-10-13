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
@NoArgsConstructor
@AllArgsConstructor
@TableName(value ="user")
@Builder
public class User implements Serializable {

    @TableId(value = "user_id",type = IdType.AUTO)
    private Long userId;

    //姓名
    @TableField(value = "user_name")
    private String userName;

    //密码
    @TableField(value = "password")
    private String password;

    //手机号
    @TableField(value = "phone")
    private String phone;

    //性别 0 女 1 男
    @TableField(value = "sex")
    private String sex;

    //地区
    @TableField(value = "place")
    private String place;

    //个人简介
    @TableField(value = "resume")
    private String resume;

    //用户头像
    @TableField(value = "portrait")
    private String portrait;

    //注册时间
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    //状态0为正常；1为异常
    @TableField(value = "status")
    private int status;

    //用户信息修改时间
    @TableField(value = "update_time")
    private LocalDateTime updateTime;
}
