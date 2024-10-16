package com.cs183.tasty.service.impl;

import com.cs183.tasty.entity.DTO.LoginDTO;
import com.cs183.tasty.entity.pojo.LoginUser;
import com.cs183.tasty.service.CommonService;
import com.cs183.tasty.utils.AliOssUtil;
import com.cs183.tasty.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.cs183.tasty.constant.MessageConstant.FILE_ERROR;
import static com.cs183.tasty.constant.MessageConstant.FILE_FORMAT_ERROR;
import static com.cs183.tasty.constant.RedisConstants.LOGIN_USER_KEY;
import static com.cs183.tasty.constant.RedisConstants.SEARCH_RECORD;
import static com.cs183.tasty.properties.AliOssProperties.MAX_FILE_SIZE;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private AliOssUtil aliOssUtil;


    @Override
    public String login(LoginDTO loginDTO) {

        //1.封装Authentication对象
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(),loginDTO.getPassword());
        //2.通过AuthenticationManager的authenticate方法来进行用户认证
        Authentication authenticated =
                authenticationManager.authenticate(authenticationToken);

        //3.从authenticated拿到用户信息
        LoginUser loginUser = (LoginUser) authenticated.getPrincipal();

        String userId = loginUser.getUser().getUserId().toString();
        //4.认证通过生成token
        String token = JwtUtil.createJWT(userId);

        //5.用户信息存入redis
        redisTemplate.opsForValue().set(LOGIN_USER_KEY + userId,loginUser);
        //6.把token返回给前端
        return token;
    }

    @Override
    public void logout() {
        //获取SecurityContextHolder中的用户id
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userId = loginUser.getUser().getUserId();
        //删除redis中的用户信息
        redisTemplate.delete(LOGIN_USER_KEY + userId);
    }

    @Override
    public List<String> getRecords() {
        //获取zset中的所有元素
        Set<String> records = stringRedisTemplate.opsForZSet().range(SEARCH_RECORD, 0, -1);
        if (records == null || records.isEmpty()) {
            return Collections.emptyList();
        }
        return records.stream().map(String::valueOf).toList();
    }

    @Override
    public CompletableFuture<String> uploadFile(MultipartFile file) throws IOException {
        //保存上传后返回的云端文件URLs
        CompletableFuture<String> future;
            //获取文件名
            String originalFilename = file.getOriginalFilename();
            //判断文件是否存在
            if(originalFilename == null){
                throw new IOException(FILE_ERROR);
            }
            // 多线程上传文件 使用supplyAsync方法,用来返回url
            future = CompletableFuture.supplyAsync(() -> {
                //判断是否为图片
                if(isImage(file)){
                    return simpleUpload(file, originalFilename);
                }else if(isVideo(file)) {
                    //要判断文件大小是否超出5MB
                    if (file.getSize() <= MAX_FILE_SIZE) {
                        //没有超出限制，就直接上传oss
                        return simpleUpload(file, originalFilename);
                    }else{
                        //如果超出限制，就要分片上传oss
                        try {
                            return aliOssUtil.fileUploadZone(file);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }else{
                    //非图片和视频抛出异常
                    throw new RuntimeException(FILE_FORMAT_ERROR);
                }
            });
        // 等待线程执行完毕
        future.join();
        return future;
    }

    private String simpleUpload(MultipartFile file, String originalFilename) {
        try {
            //截取原始文件名的后缀   dfdfdf.png
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            //构造新文件名称
            String objectName = UUID.randomUUID() + extension;
            // 设置上传到云存储的路径
            return aliOssUtil.upload(file.getBytes(), objectName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isImage(MultipartFile multipartFile) {
        File file = aliOssUtil.multipartFileToFile(multipartFile);
        //判断file是否为图片格式
        if (file == null) {
            return false;
        }
        Image img;
        try {
            img = ImageIO.read(file);
            return img != null && img.getWidth(null) > 0 && img.getHeight(null) > 0;
        } catch (Exception e) {
            return false;
        } finally {
            file.delete();
        }
    }



    private static boolean isVideo(MultipartFile multipartFile){

        List<String> formatList = new ArrayList<>();

        formatList.add("avi");
        formatList.add("flv");
        formatList.add("mov");
        formatList.add("mp4");
        formatList.add("mpg");
        formatList.add("mpeg");
        formatList.add("mpv");
        formatList.add("navi");
        formatList.add("qt");
        formatList.add("rm");
        formatList.add("ram");
        formatList.add("ram");
        formatList.add("ram");
        formatList.add("swf");
        formatList.add("wmv");

        String originalFilename = multipartFile.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        for (String s : formatList) {
            if (extension.equalsIgnoreCase(s)) {
                return true;
            }
        }

        return false;
    }


}
