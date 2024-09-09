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

    //Send verification code
    @Override
    public boolean sendCode(String phone, String code) throws ClientException {
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
        if (redisTemplate.hasKey("R_VERIFY_CODE")){
            request.setTemplateCode(smsCodeProperty.templateId_R);
        }else if (redisTemplate.hasKey("F_VERIFY_CODE")){
            request.setTemplateCode(smsCodeProperty.templateId_F);
        }
        request.setTemplateParam("{\"code\":\"" + code + "\"}");
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
        //Check whether the SMS message is successfully sent
        if (sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
            System.out.println("SMS sent successfully！");
            return true;
        } else {
            System.out.println("SMS sent failure！");
            return false;
        }
        /*Config config = new com.aliyun.teaopenapi.models.Config()
                // 必填，请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_ID。
                .setAccessKeyId(System.getenv("LTAI5tRWfNNxbn7c5PRUH3Cz"))
                // 必填，请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_SECRET。
                .setAccessKeySecret(System.getenv("ZQqr85w3gbmsGPTtta4G4jNPHObxYH"));
        // Endpoint 请参考 https://api.aliyun.com/product/Dysmsapi
        config.endpoint = "dysmsapi.aliyuncs.com";
        Client client = new Client(config);
        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setSignName("tasty1")
                .setTemplateCode(smsCodeProperty.templateId)
                .setPhoneNumbers(phone)
                .setTemplateParam("{\"code\":\"" + code + "\"}");
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            client.sendSmsWithOptions(sendSmsRequest, runtime);
        } catch (TeaException error) {
            // 此处仅做打印展示，请谨慎对待异常处理，在工程项目中切勿直接忽略异常。
            // 错误 message
            System.out.println(error.getMessage());
            // 诊断地址
            System.out.println(error.getData().get("Recommend"));
            com.aliyun.teautil.Common.assertAsString(error.message);
            return false;
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 此处仅做打印展示，请谨慎对待异常处理，在工程项目中切勿直接忽略异常。
            // 错误 message
            System.out.println(error.getMessage());
            // 诊断地址
            System.out.println(error.getData().get("Recommend"));
            com.aliyun.teautil.Common.assertAsString(error.message);
            return false;
        }
        return true;*/
    }

    @Override
    public boolean forgetVerifyCode(String veifyCode, String code) {
        veifyCode = (String) redisTemplate.opsForValue().get(KEY_F);
        return veifyCode.equals(code);
    }
}



