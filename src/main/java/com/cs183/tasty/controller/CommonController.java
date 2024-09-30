package com.cs183.tasty.controller;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.cs183.tasty.common.Result;
import com.cs183.tasty.constant.MessageConstant;
import com.cs183.tasty.entity.DTO.LoginDTO;
import com.cs183.tasty.entity.DTO.UserRegisterDTO;
import com.cs183.tasty.properties.SmsCodeProperty;
import com.cs183.tasty.service.CommonService;
import com.cs183.tasty.service.SmsService;
import com.cs183.tasty.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {
    @Autowired
    private AliOssUtil aliOssUtil;
    @Autowired
    private SmsCodeProperty smsCodeProperty;
    @Autowired
    private SmsService smsService;
    @Autowired
    private CommonService commonService;

    //结合SpringSecurity安全框架登录
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result<Object> Login(@RequestBody LoginDTO loginDTO) {
        String token = commonService.login(loginDTO);
        log.info("登录成功token：{}",token);
        //返回token到前端
        return Result.ok(token);
    }

    //发送验证码接口
    @RequestMapping(value = "/code",method = RequestMethod.POST)
    public Result<Object> sendCode(@RequestParam("phone") String phone) throws ClientException {
        smsService.sendCode(phone);
        return Result.ok();
    }
    //    public Result<UserLoginVo> Login(@RequestBody UserLoginDTO userLoginDTO) {
//        log.info("User name Password Indicates the login：{},{}",
//                userLoginDTO.getUsername(), userLoginDTO.getPassword());
//        UserLoginVo user = userService.userLogin(userLoginDTO);
//        if (user.getInfo().equals(LOGIN_SUCCESSFUL)) {
//            return Result.ok(user);
//        }else{
//            return Result.fail(user.getInfo());
//        }
//    }


    @RequestMapping(value = "/logout",method = RequestMethod.POST)
    public Result<Object> logout(){
        commonService.logout();
        return Result.ok();
    }

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file){
        log.info("File upload：{}",file);

        try {
            //原始文件名
            String originalFilename = file.getOriginalFilename();
            //截取原始文件名的后缀   dfdfdf.png
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            //构造新文件名称
            String objectName = UUID.randomUUID().toString() + extension;

            //文件的请求路径
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.ok(filePath);
        } catch (IOException e) {
            log.error("File upload failure：{}", e);
        }

        return Result.fail(MessageConstant.UPLOAD_FAILED);
    }

    }
