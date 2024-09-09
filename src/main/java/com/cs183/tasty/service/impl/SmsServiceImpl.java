package com.cs183.tasty.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.dysmsapi20170525.models.*;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyun.tea.TeaException;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.cs183.tasty.entity.DTO.UserRegisterDTO;
import com.cs183.tasty.entity.Vo.UserRegisterVo;
import com.cs183.tasty.entity.pojo.User;
import com.cs183.tasty.mapper.UserServiceMapper;
import com.cs183.tasty.properties.SmsCodeProperty;
import com.cs183.tasty.service.SmsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;

import static com.cs183.tasty.constant.MessageConstant.*;



@Service
public class SmsServiceImpl implements SmsService {

    public static final String KEY_R = "R_VERIFY_CODE";

    public static final String KEY_F = "F_VERIFY_CODE";

    @Autowired
    private SmsCodeProperty smsCodeProperty;

    @Autowired
    private UserServiceMapper userServiceMapper;

    @Autowired
    private RedisTemplate redisTemplate;
    //生成验证码
    @Override
    public String creatCode() {
        return RandomUtil.randomNumbers(4);
    }

    @Override
    public UserRegisterVo registerVerifyCode(String code, String userCode, String userRegisterDTO) {
        code = (String) redisTemplate.opsForValue().get(KEY_R);
        if (code.equals(userCode)){
            User user = User.builder().
                    createTime(LocalDateTime.now()).
                    updateTime(LocalDateTime.now()).
                    status(1).
                    build();
            System.out.println(userRegisterDTO);

            userRegisterDTO = userRegisterDTO.replace("UserRegisterDTO(", "").replace(")", "");

            // 按逗号分割
            String[] fields = userRegisterDTO.split(", ");

            // 创建并设置源对象
            UserRegisterDTO sourceUser = new UserRegisterDTO();
            for (String field : fields) {
                String[] keyValue = field.split("=");
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim();

                    switch (key) {
                        case "userName":
                            sourceUser.setUserName(value);
                            break;
                        case "password":
                            sourceUser.setPassword(value);
                            break;
                        case "phone":
                            sourceUser.setPhone(value);
                            break;
                        default:
                            System.out.println("未知的字段: " + key);
                    }
                } else {
                    System.out.println("解析错误的字段: " + field);
                }
            }
            System.out.println(sourceUser);
            BeanUtils.copyProperties(sourceUser, user);
            userServiceMapper.insert(user);
            return UserRegisterVo.builder()
                    .info(REGISTER_SUCCESSFUL)
                    .build();
        }else {
            return UserRegisterVo.builder()
                    .info(NUMBER_CODE_NOT_EQUAL)
                    .build();
        }
    }

    
    @Override
    public boolean forgetVerifyCode(String veifyCode, String code) {
        veifyCode = (String) redisTemplate.opsForValue().get(KEY_F);
        return veifyCode.equals(code);
    }
}



