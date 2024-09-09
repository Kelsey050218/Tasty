package com.cs183.tasty.entity.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
@Data
@TableName(value ="user")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo implements Serializable {

    @JsonProperty("username")
    private String username;

    @JsonProperty("sex")
    private String sex;

    @JsonProperty("place")
    private String place;

    @JsonProperty("resume")
    private String resume;

    @JsonProperty("portrait")
    private String portrait;
}
