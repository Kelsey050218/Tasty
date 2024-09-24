package com.cs183.tasty.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.cs183.tasty.entity.DTO.UserRegisterDTO;
import com.cs183.tasty.entity.pojo.User;
import com.cs183.tasty.mapper.UserServiceMapper;
import com.cs183.tasty.properties.SmsCodeProperty;
import com.cs183.tasty.service.SmsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static com.cs183.tasty.constant.MessageConstant.*;
import static com.cs183.tasty.utils.RedisConstants.*;


@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    private SmsCodeProperty smsCodeProperty;

    @Autowired
    private RedisTemplate redisTemplate;

//    @Override
//    public void registerVerifyCode(String code, String userCode, String userRegisterDTO) {
//        code = (String) redisTemplate.opsForValue().get(R_VERIFY_CODE);
//        if (code.equals(userCode)){
//            User user = User.builder().
//                    createTime(LocalDateTime.now()).
//                    updateTime(LocalDateTime.now()).
//                    status(1).
//                    build();
//            System.out.println(userRegisterDTO);
//
//            userRegisterDTO = userRegisterDTO.replace("UserRegisterDTO(", "").replace(")", "");
//
//            // 按逗号分割
//            String[] fields = userRegisterDTO.split(", ");
//
//            // 创建并设置源对象
//            UserRegisterDTO sourceUser = new UserRegisterDTO();
//            for (String field : fields) {
//                String[] keyValue = field.split("=");
//                if (keyValue.length == 2) {
//                    String key = keyValue[0].trim();
//                    String value = keyValue[1].trim();
//
//                    switch (key) {
//                        case "userName":
//                            sourceUser.setUserName(value);
//                            break;
//                        case "password":
//                            sourceUser.setPassword(value);
//                            break;
//                        case "phone":
//                            sourceUser.setPhone(value);
//                            break;
//                        default:
//                            System.out.println("未知的字段: " + key);
//                    }
//                } else {
//                    System.out.println("解析错误的字段: " + field);
//                }
//            }
//            System.out.println(sourceUser);
//            BeanUtils.copyProperties(sourceUser, user);
//            userServiceMapper.insert(user);
//            return UserRegisterVo.builder()
//                    .info(REGISTER_SUCCESSFUL)
//                    .build();
//        }else {
//            return UserRegisterVo.builder()
//                    .info(NUMBER_CODE_NOT_EQUAL)
//                    .build();
//        }
//    }

    //Send verification code
    @Override
    public void sendCode(String phone) throws ClientException {
        //Generate a four-digit verification code
        String code = RandomUtil.randomNumbers(4);
        //Set the timeout period
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        //Initialize ascClient
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou",
                smsCodeProperty.accessKeyID, smsCodeProperty.accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou",
                "Dysmsapi", "dysmsapi.aliyuncs.com");
        IAcsClient acsClient = new DefaultAcsClient(profile);
        //Assemble request object
        SendSmsRequest request = new SendSmsRequest();
        request.setPhoneNumbers(phone);
        request.setSignName(smsCodeProperty.signName);
        if (redisTemplate.hasKey(VERIFY_CODE)) {
            request.setTemplateCode(smsCodeProperty.templateId_R);
        }
//        } else if (redisTemplate.hasKey("F_VERIFY_CODE")) {
//            request.setTemplateCode(smsCodeProperty.templateId_F);
//        }
        request.setTemplateParam("{\"code\":\"" + code + "\"}");
        request.setTemplateCode(code);
        redisTemplate.opsForValue().set(VERIFY_CODE, code, VERIFY_CODE_TTL, TimeUnit.MINUTES);
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
        //Check whether the SMS message is successfully sent
        if (sendSmsResponse.getCode() == null) {
            redisTemplate.delete(VERIFY_CODE);
            throw new ClientException("验证码发送失败");
        }
    }
}


