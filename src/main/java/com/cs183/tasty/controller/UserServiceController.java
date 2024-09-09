package com.cs183.tasty.controller;

import com.cs183.tasty.common.Result;
import com.cs183.tasty.constant.MessageConstant;
import com.cs183.tasty.entity.DTO.ForgetDTO;
import com.cs183.tasty.entity.DTO.UserRegisterDTO;
import com.cs183.tasty.entity.DTO.UserLoginDTO;
import com.cs183.tasty.entity.pojo.Motivation;
import com.cs183.tasty.entity.pojo.UserInfo;
import com.cs183.tasty.entity.Vo.UserLoginVo;
import com.cs183.tasty.entity.Vo.UserRegisterVo;
import com.cs183.tasty.service.SmsService;
import com.cs183.tasty.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.cs183.tasty.constant.MessageConstant.*;


@RestController
@Slf4j
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserServiceController implements HandlerInterceptor {
    public static final String KEY_R = "R_VERIFY_CODE";
    public static final String KEY_F = "F_VERIFY_CODE";

    private final UserService userService;
    private final SmsService smsService;
    private final RedisTemplate redisTemplate;

    //User name Password Indicates the login
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result<UserLoginVo> Login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("User name Password Indicates the login：{},{}",
                userLoginDTO.getUsername(), userLoginDTO.getPassword());
        UserLoginVo user = userService.userLogin(userLoginDTO);
        if (user.getInfo().equals(LOGIN_SUCCESSFUL)) {
            return Result.ok(user);
        }else{
            return Result.fail(user.getInfo());
        }
    }
    //User registration
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Result<UserRegisterVo> register(@RequestBody UserRegisterDTO userRegisterDTO) throws Exception {
        log.info("User Register：{}",userRegisterDTO.getPassword());
        UserRegisterVo user = userService.userRegister(userRegisterDTO);
        redisTemplate.opsForValue().set("UserInfo",userRegisterDTO.toString());
        if (user.getInfo().equals(SMS_SEND_SUCCESSFULLY)) {
            return Result.ok(user.getInfo());
        }else{
            return Result.fail(user.getInfo());
        }
    }

    @Transactional
    @RequestMapping(value = "/registerVerifyCode",method = RequestMethod.GET)
    public Result<UserRegisterVo> registerVerifyCode(String code) {
        log.info("verify the code:{}", code);
        String veifyCode = (String) redisTemplate.opsForValue().get(KEY_R);
        String userInfo = (String) redisTemplate.opsForValue().get("UserInfo");
        UserRegisterVo user = smsService.registerVerifyCode(veifyCode, code, userInfo);
        if (user.getInfo().equals(REGISTER_SUCCESSFUL)) {
            redisTemplate.delete(KEY_R);
            return Result.ok(user.getInfo());
        } else {
            return Result.fail(user.getInfo());
        }
    }

    @RequestMapping(value = "/logout",method = RequestMethod.POST)
    public Result<String> logout(){
        redisTemplate.delete("UserInfo");
        return Result.ok("Redirects to the login screen");
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public Result<UserInfo> getById(@PathVariable Long id){
        UserInfo userInfo = userService.getById(id);
        return Result.ok(userInfo);
    }

    @RequestMapping(value = "/update",method = RequestMethod.PUT)
    public Result<String> update(@RequestBody UserInfo userInfo){
        log.info("Users modify personal information：{}",userInfo);
        userService.updateInfo(userInfo);
        return Result.ok();
    }

    //User forgets password - Change password
    @RequestMapping(value = "/forgetPassword",method = RequestMethod.POST)
    public Result<String> forget(@RequestBody ForgetDTO forgetDTO) throws Exception {
        log.info("User forgets password:{}",forgetDTO);
        userService.forgetPassword(forgetDTO);
        return Result.ok();
    }

    @Transactional
    @RequestMapping(value = "/forgetVerifyCode",method = RequestMethod.GET)
    public Result<UserRegisterVo> forgetVerifyCode(String code) {
        log.info("verify the code:{}", code);
        String veifyCode = (String) redisTemplate.opsForValue().get(KEY_F);
        boolean flag = smsService.forgetVerifyCode(veifyCode, code);
        if (flag) {
            redisTemplate.delete(KEY_F);
            userService.updatePassword();
            redisTemplate.delete("User");
            return Result.ok();
        } else {
            return Result.fail(PASSWORD_EDIT_FAILED);
        }
    }

    @RequestMapping(value = "/motivation",method = RequestMethod.GET)
    public Result<String> getMotivation(){
        String sentence = userService.getSentence();
        return Result.ok(sentence);
    }
    }



