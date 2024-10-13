package com.cs183.tasty.controller;

import com.cs183.tasty.common.Result;
import com.cs183.tasty.entity.DTO.ForgetDTO;
import com.cs183.tasty.entity.DTO.UserRegisterDTO;
import com.cs183.tasty.entity.pojo.User;
import com.cs183.tasty.entity.pojo.UserInfo;
import com.cs183.tasty.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Slf4j
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserServiceController {

    @Autowired
    private UserService userService;

    //用户注册
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Result<Object> register(@RequestBody UserRegisterDTO userRegisterDTO) throws Exception {
        log.info("User Register：{}",userRegisterDTO.getPassword());
        userService.userRegister(userRegisterDTO);
        return Result.ok();
    }

    //用户信息查询
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public Result<UserInfo> getById(@PathVariable Long id){
        UserInfo userInfo = userService.getById(id);
        return Result.ok(userInfo);
    }

    //更新用户信息
    @RequestMapping(value = "/update",method = RequestMethod.PUT)
    public Result<String> update(@RequestBody UserInfo userInfo){
        log.info("Users modify personal information：{}",userInfo);
        userService.updateInfo(userInfo);
        return Result.ok();
    }

    //忘记密码
    @RequestMapping(value = "/forgetPassword",method = RequestMethod.POST)
    public Result<String> forget(@RequestBody ForgetDTO forgetDTO) throws Exception {
        log.info("User forgets password:{}",forgetDTO);
        userService.forgetPassword(forgetDTO);
        return Result.ok();
    }

    //每日一句
    @RequestMapping(value = "/motivation",method = RequestMethod.GET)
    public Result<String> getMotivation(){
        String sentence = userService.getSentence();
        return Result.ok(sentence);
    }

    //关注和取关
    @RequestMapping(value = "/follow/{id}/{isFollow}")
    public Result<Object> follow(@PathVariable("id") Long followUserId,@PathVariable("isFollow") boolean isFollow) throws Exception {
        userService.follow(followUserId,isFollow);
        return Result.ok();
    }

    //查询粉丝列表
    @RequestMapping(value = "/fans/{id}")
    public Result<List<User>> fans(@PathVariable Long id){
        List<User> fans = userService.getFans(id);
        return Result.ok(fans);
    }

    //查询关注列表
    @RequestMapping(value = "/follow/{id}")
    public Result<List<User>> getFollowList(@PathVariable Long id){
        List<User> follow_list = userService.getFollowList(id);
        return Result.ok(follow_list);
    }

    //查询当前用户的互关好友
    @RequestMapping(value = "/mutual/fans")
    public Result<List<User>> mutualFans(){
        List<User> list = userService.getMutualFans();
        return Result.ok(list);
    }

    //搜索用户（动态搜索）
    @RequestMapping(value = "/search")
    public Result<List<User>> searchUser(@RequestParam(required = false) String username,
                                         @RequestParam(required = false) String phone){
        List<User> userList = userService.conditionSearch(username,phone);
        return Result.ok(userList);
    }



}



