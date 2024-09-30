package com.cs183.tasty.controller;

import com.cs183.tasty.common.Result;
import com.cs183.tasty.entity.DTO.AdminRegisterDTO;
import com.cs183.tasty.entity.DTO.UserRegisterDTO;
import com.cs183.tasty.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public Result<Object> register(@RequestBody AdminRegisterDTO adminRegisterDTO) throws Exception {
        log.info("User Register：{}",adminRegisterDTO.getPassword());
        adminService.adminRegister(adminRegisterDTO);
        return Result.ok();
    }

    @RequestMapping(value = "/handleReport/{id}", method = RequestMethod.PUT)
    public Result<Object> handleReport(@PathVariable Long id,@RequestParam Integer isSuccess){
        adminService.handleReport(id,isSuccess);
        return Result.ok();
    }











}
