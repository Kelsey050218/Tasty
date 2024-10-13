package com.cs183.tasty.service;

import com.cs183.tasty.entity.DTO.LoginDTO;

public interface CommonService {

    //登录
    String login(LoginDTO LoginDTO);

    void logout();
}
