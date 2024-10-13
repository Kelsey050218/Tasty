package com.cs183.tasty.service.impl;

import com.cs183.tasty.entity.DTO.LoginDTO;
import com.cs183.tasty.entity.pojo.LoginUser;
import com.cs183.tasty.service.CommonService;
import com.cs183.tasty.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.cs183.tasty.constant.RedisConstants.LOGIN_USER_KEY;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommonServiceImpl implements CommonService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public String login(LoginDTO loginDTO) {

        //1.封装Authentication对象
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDTO.getName(),loginDTO.getPassword());
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

}
