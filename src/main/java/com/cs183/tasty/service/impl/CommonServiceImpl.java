package com.cs183.tasty.service.impl;

import cn.hutool.core.util.StrUtil;
import com.cs183.tasty.entity.DTO.LoginDTO;
import com.cs183.tasty.entity.DTO.UserRegisterDTO;
import com.cs183.tasty.entity.pojo.LoginUser;
import com.cs183.tasty.entity.pojo.User;
import com.cs183.tasty.service.CommonService;
import com.cs183.tasty.utils.JwtUtil;
import com.github.yulichang.query.MPJQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.cs183.tasty.constant.MessageConstant.*;
import static com.cs183.tasty.utils.RedisConstants.LOGIN_USER_KEY;
import static com.cs183.tasty.utils.RedisConstants.VERIFY_CODE;
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


//        String username = userLoginDTO.getUsername();
//        String password = userLoginDTO.getPassword();
//        MPJQueryWrapper<User> wrapper = new MPJQueryWrapper<>();
//        wrapper.selectAll(User.class)
//                .eq("user_name", username);
//        //1、Query the data in the database based on the user name
//        User user = userServiceMapper.selectOne(wrapper);
//        //2、Handle various exceptions
//        if (user == null) {
//            //Account does not exist
//            return UserLoginVo.builder()
//                    .info(ACCOUNT_NOT_FOUND)
//                    .build();
//        }
//        if (!password.equals(user.getPassword())) {
//            //Password error
//            return UserLoginVo.builder()
//                    .info(PASSWORD_ERROR)
//                    .build();
//        }
//        if (user.getStatus() == 0) {
//            //Account status abnormal
//            return UserLoginVo.builder()
//                    .info(ACCOUNT_LOCKED)
//                    .build();
//        }
//        //Generate a jwt token for the user
//        Map<String, Object> claims = new HashMap<>();
//        claims.put(jwtClaimsConstant.USER_ID, user.getUserId());
//        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
//        UserLoginVo userLoginVO = UserLoginVo.builder()
//                .userId(user.getUserId())
//                .status(1)
//                .token(token)
//                .info(LOGIN_SUCCESSFUL)
//                .build();
//        //Returns the user object
//        return userLoginVO;
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
