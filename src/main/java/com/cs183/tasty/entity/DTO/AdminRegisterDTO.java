package com.cs183.tasty.entity.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class AdminRegisterDTO implements Serializable {

    @JsonProperty("adminName")
    private String adminName;
    @JsonProperty("password")
    private String password;
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("code")
    private String code;

    @Override
    public String toString() {
        return "adminName= " + adminName  +
                ", password= " + password +
                ", phone= " + phone +
                ", code= " + code;
    }
}
