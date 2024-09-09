package com.cs183.tasty.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs183.tasty.entity.DTO.ForgetDTO;
import com.cs183.tasty.entity.DTO.UserRegisterDTO;
import com.cs183.tasty.entity.DTO.UserLoginDTO;
import com.cs183.tasty.entity.Vo.UserLoginVo;
import com.cs183.tasty.entity.Vo.UserRegisterVo;
import com.cs183.tasty.entity.pojo.Motivation;
import com.cs183.tasty.entity.pojo.User;
import com.cs183.tasty.entity.pojo.UserInfo;


public interface UserService extends IService<User> {
    //登录
    UserLoginVo userLogin(UserLoginDTO userLoginDTO);

    //注册
    UserRegisterVo userRegister(UserRegisterDTO userRegisterDTO) throws Exception;

    UserInfo getById(Long id);

    void updateInfo(UserInfo userInfo);

    void forgetPassword(ForgetDTO forgetDTO) throws Exception;

    void updatePassword();

    String getSentence();
}
