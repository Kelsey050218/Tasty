package com.cs183.tasty.service;

import com.cs183.tasty.entity.DTO.AdminRegisterDTO;

public interface AdminService {
    void adminRegister(AdminRegisterDTO adminRegisterDTO) throws Exception;

    void handleReport(Long id,Integer isSuccess);

    void handleUser(Long id);
}
