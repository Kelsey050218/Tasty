package com.cs183.tasty.entity.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForgetDTO implements Serializable {

    @JsonProperty("newPassword")
    private String newPassword;
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("code")
    private String code;

    @Override
    public String toString() {
        return "newPassword= " + newPassword +
                ", phone= " + phone +
                ", code= " + code;
    }
}
