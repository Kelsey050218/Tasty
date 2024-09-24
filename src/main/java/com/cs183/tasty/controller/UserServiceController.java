package com.cs183.tasty.controller;

import com.aliyuncs.exceptions.ClientException;
import com.cs183.tasty.common.Result;
import com.cs183.tasty.entity.DTO.ForgetDTO;
import com.cs183.tasty.entity.DTO.UserRegisterDTO;
import com.cs183.tasty.entity.DTO.UserLoginDTO;
import com.cs183.tasty.entity.pojo.UserInfo;
import com.cs183.tasty.service.SmsService;
import com.cs183.tasty.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserService userService;

    @Autowired
    private SmsService smsService;

    //结合SpringSecurity安全框架登录
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result<Object> Login(@RequestBody UserLoginDTO userLoginDTO) {
        String token = userService.userLogin(userLoginDTO);
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
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Result<Object> register(@RequestBody UserRegisterDTO userRegisterDTO) throws Exception {
        log.info("User Register：{}",userRegisterDTO.getPassword());
        userService.userRegister(userRegisterDTO);
        return Result.ok();
    }


    @RequestMapping(value = "/logout",method = RequestMethod.POST)
    public Result<Object> logout(){
        userService.logout();
        return Result.ok();
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

    @RequestMapping(value = "/motivation",method = RequestMethod.GET)
    public Result<String> getMotivation(){
        String sentence = userService.getSentence();
        return Result.ok(sentence);
    }
    }



