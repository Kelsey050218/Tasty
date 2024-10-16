package com.cs183.tasty.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cs183.tasty.entity.DTO.ForgetDTO;
import com.cs183.tasty.entity.DTO.UserRegisterDTO;
import com.cs183.tasty.entity.pojo.User;
import com.cs183.tasty.entity.pojo.UserInfo;

import java.util.List;


public interface UserService extends IService<User> {

    //注册
    void userRegister(UserRegisterDTO registerDTO) throws Exception;

    UserInfo getById(Long id);

    void updateInfo(UserInfo userInfo);

    void forgetPassword(ForgetDTO forgetDTO) throws Exception;

    String getSentence();

    void follow(Long followUserId, boolean isFollow) throws Exception;

    List<User> getFans(Long id);

    List<User> getFollowList(Long id);

    List<User> getMutualFans();

    List<User> conditionSearch(String username,String phone);
}
