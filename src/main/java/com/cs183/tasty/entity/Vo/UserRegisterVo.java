package com.cs183.tasty.entity.Vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
public class UserRegisterVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("info")
    private String info;

    @JsonProperty("code")
    private String code;

}