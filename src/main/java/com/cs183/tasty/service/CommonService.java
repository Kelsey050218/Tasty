package com.cs183.tasty.service;

import com.cs183.tasty.entity.DTO.LoginDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CommonService {

    //登录
    String login(LoginDTO LoginDTO);

    void logout();

    List<String> getRecords();

    CompletableFuture<String> uploadFile(MultipartFile file) throws IOException;
}
