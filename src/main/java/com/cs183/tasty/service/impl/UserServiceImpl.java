package com.cs183.tasty.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs183.tasty.constant.MessageConstant;
import com.cs183.tasty.entity.DTO.ForgetDTO;
import com.cs183.tasty.entity.DTO.UserRegisterDTO;
import com.cs183.tasty.entity.pojo.*;
import com.cs183.tasty.mapper.MenuMapper;
import com.cs183.tasty.mapper.MotivationMapper;
import com.cs183.tasty.mapper.UserServiceMapper;
import com.cs183.tasty.service.SmsService;
import com.cs183.tasty.service.UserService;
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
import java.util.Random;

import static com.cs183.tasty.constant.MessageConstant.*;
import static com.cs183.tasty.utils.RedisConstants.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserServiceMapper, User> implements UserService {
    public static final String KEY_M = "Motivation";


    @Autowired
    private UserServiceMapper userServiceMapper;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private SmsService smsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MotivationMapper motivationMapper;

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public void userRegister(UserRegisterDTO userRegisterDTO) throws Exception {
        //Determine whether the user exists
        String username = userRegisterDTO.getUserName();
        MPJQueryWrapper<User> wrapper = new MPJQueryWrapper<>();
        wrapper.selectAll(User.class)
                .eq("user_name", username);
        User user = userServiceMapper.selectOne(wrapper);
        //The user already exists. Please log in directly
        if (user != null) {
            throw new Exception(ALREADY_EXISTS);
        }
        //Check verification code
        String code = (String) redisTemplate.opsForValue().get(VERIFY_CODE);
        if(StrUtil.isBlank(code)){
            throw new Exception(NUMBER_CODE_EXPIRED);
        }
        if(userRegisterDTO.getCode().equals(code)){
            redisTemplate.delete(VERIFY_CODE);
            User newUser = new User();
            BeanUtils.copyProperties(userRegisterDTO,newUser);
            newUser.setCreateTime(LocalDateTime.now());
            userServiceMapper.insert(newUser);
            menuMapper.bondUserRole(newUser.getUserId());
        }else{
            redisTemplate.delete(VERIFY_CODE);
            throw new Exception(NUMBER_CODE_NOT_EQUAL);
        }
    }

    @Override
    public UserInfo getById(Long id) {
        User user = userServiceMapper.selectById(id);
        UserInfo userInfo = UserInfo.builder()
                .username(user.getUserName())
                .sex(user.getSex())
                .place(user.getPlace())
                .resume(user.getResume())
                .portrait(user.getPortrait())
                .build();
        return userInfo;
    }

    @Override
    public void updateInfo(UserInfo userInfo) {
        String userName = userInfo.getUsername();
        User user = userServiceMapper.selectOne(Wrappers.lambdaQuery(User.class).eq(User::getUserName, userName));
        BeanUtils.copyProperties(userInfo, user);
        user.setUpdateTime(LocalDateTime.now());
        userServiceMapper.updateById(user);
    }

    @Override
    public void forgetPassword(ForgetDTO forgetDTO) throws Exception {
        //Determine whether the user exists
        String phoneNumber = forgetDTO.getPhone();
        MPJQueryWrapper<User> wrapper = new MPJQueryWrapper<>();
        wrapper.selectAll(User.class)
                .eq("phone", phoneNumber);
        User user = userServiceMapper.selectOne(wrapper);
        if (user == null) {
            throw new Exception(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        //Send verification code
        smsService.sendCode(forgetDTO.getPhone());
        //Check verification code
        String code = (String) redisTemplate.opsForValue().get(VERIFY_CODE);
        if(StrUtil.isBlank(code)){
            throw new Exception(NUMBER_CODE_EXPIRED);
        }
        if(forgetDTO.getCode().equals(code)){
            redisTemplate.delete(VERIFY_CODE);
            user.setPassword(forgetDTO.getNewPassword());
            userServiceMapper.updateById(user);
        }else{
            redisTemplate.delete(VERIFY_CODE);
            throw new Exception(NUMBER_CODE_NOT_EQUAL);
        }
    }

    @Override
    public String getSentence() {
        boolean flag = redisTemplate.hasKey(KEY_M);
        if (flag) {
            String sentence = (String) redisTemplate.opsForValue().get(KEY_M);
            sentence = sentence.replaceAll("\\u0000", "");
            return sentence;
        } else {
            Random random = new Random();
            int randomNumber = random.nextInt(1, 11);
            Motivation motivation = motivationMapper.selectById(randomNumber);
            redisTemplate.opsForValue().set(KEY_M, motivation.getSentence(), 60 * 60 * 24);
            return motivation.getSentence();
        }
    }
}
