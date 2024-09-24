package com.cs183.tasty.entity.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

@Data
    public class UserRegisterDTO implements Serializable {
        @JsonProperty("userName")
        private String userName;
        @JsonProperty("password")
        private String password;
        @JsonProperty("phone")
        private String phone;
        @JsonProperty("code")
        private String code;

    @Override
    public String toString() {
        return "userName= " + userName  +
                ", password= " + password +
                ", phone= " + phone +
                ", code= " + code;
    }
}




