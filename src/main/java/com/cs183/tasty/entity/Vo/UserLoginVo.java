package com.cs183.tasty.entity.Vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
public class UserLoginVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("status")
    private int status;

    @JsonProperty("token")
    private String token;

    @JsonProperty("info")
    private String info;

}