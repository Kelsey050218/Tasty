package com.cs183.tasty.service;

import com.aliyuncs.exceptions.ClientException;

public interface SmsService {

    void sendCode(String phone) throws ClientException;

}
