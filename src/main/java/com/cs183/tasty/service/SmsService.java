package com.cs183.tasty.service;

import com.aliyuncs.exceptions.ClientException;
import com.cs183.tasty.entity.DTO.UserRegisterDTO;
import com.cs183.tasty.entity.Vo.UserRegisterVo;

public interface SmsService {
    //Get a random verification code
    String creatCode ();

    //Check verification code
    UserRegisterVo registerVerifyCode(String code, String userCode, String userRegisterDTO);

    boolean sendCode(String phone,String code) throws ClientException;

    boolean forgetVerifyCode(String veifyCode, String code);
}
