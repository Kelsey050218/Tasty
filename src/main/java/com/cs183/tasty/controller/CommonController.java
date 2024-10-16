package com.cs183.tasty.controller;

import com.aliyuncs.exceptions.ClientException;
import com.cs183.tasty.common.Result;
import com.cs183.tasty.entity.DTO.LoginDTO;
import com.cs183.tasty.service.CommonService;
import com.cs183.tasty.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

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

    //登出接口：删除redis中的用户信息
    @RequestMapping(value = "/logout",method = RequestMethod.POST)
    public Result<Object> logout(){
        commonService.logout();
        return Result.ok();
    }

    //上传文件
    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    public Result<String> upload(MultipartFile file) throws IOException, ExecutionException, InterruptedException {
        CompletableFuture<String> url = commonService.uploadFile(file);
       return Result.ok(url.get());
    }

    //获取所有历史搜索记录
    @RequestMapping(value = "/search/record",method = RequestMethod.GET)
    public Result<List<String>> searchRecord(){
       List<String> records = commonService.getRecords();
       return Result.ok(records);

    }




    }
