package com.cs183.tasty.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs183.tasty.constant.MessageConstant;
import com.cs183.tasty.constant.jwtClaimsConstant;
import com.cs183.tasty.context.BaseContext;
import com.cs183.tasty.entity.DTO.ForgetDTO;
import com.cs183.tasty.entity.DTO.UserRegisterDTO;
import com.cs183.tasty.entity.DTO.UserLoginDTO;
import com.cs183.tasty.entity.Vo.UserLoginVo;
import com.cs183.tasty.entity.Vo.UserRegisterVo;
import com.cs183.tasty.entity.pojo.Motivation;
import com.cs183.tasty.entity.pojo.Recipe;
import com.cs183.tasty.entity.pojo.User;
import com.cs183.tasty.entity.pojo.UserInfo;
import com.cs183.tasty.mapper.MotivationMapper;
import com.cs183.tasty.mapper.UserServiceMapper;
import com.cs183.tasty.service.SmsService;
import com.cs183.tasty.service.UserService;
import com.cs183.tasty.properties.JwtProperties;
import com.cs183.tasty.utils.JwtUtil;
import com.github.yulichang.query.MPJQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Wrapper;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.cs183.tasty.constant.MessageConstant.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserServiceMapper, User> implements UserService {
    public static final String KEY_F = "F_VERIFY_CODE";
    public static final String KEY_R = "R_VERIFY_CODE";
    public static final String KEY_M = "Motivation";


    @Autowired
    private UserServiceMapper userServiceMapper;

    @Autowired
    private SmsService smsService;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MotivationMapper motivationMapper;

    @Override
    public UserLoginVo userLogin(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();
        MPJQueryWrapper<User> wrapper = new MPJQueryWrapper<>();
        wrapper.selectAll(User.class)
                .eq("user_name", username);
        //1、Query the data in the database based on the user name
        User user = userServiceMapper.selectOne(wrapper);
        //2、Handle various exceptions
        if (user == null) {
            //Account does not exist
            return UserLoginVo.builder()
                    .info(ACCOUNT_NOT_FOUND)
                    .build();
        }
        if (!password.equals(user.getPassword())) {
            //Password error
            return UserLoginVo.builder()
                    .info(PASSWORD_ERROR)
                    .build();
        }
        if (user.getStatus() == 0) {
            //Account status abnormal
            return UserLoginVo.builder()
                    .info(ACCOUNT_LOCKED)
                    .build();
        }
        //Generate a jwt token for the user
        Map<String, Object> claims = new HashMap<>();
        claims.put(jwtClaimsConstant.USER_ID, user.getUserId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
        UserLoginVo userLoginVO = UserLoginVo.builder()
                .userId(user.getUserId())
                .status(1)
                .token(token)
                .info(LOGIN_SUCCESSFUL)
                .build();
        //Returns the user object
        return userLoginVO;
    }

    @Override
    public UserRegisterVo userRegister(UserRegisterDTO userRegisterDTO) throws Exception {
        //Determine whether the user exists
        String username = userRegisterDTO.getUserName();
        MPJQueryWrapper<User> wrapper = new MPJQueryWrapper<>();
        wrapper.selectAll(User.class)
                .eq("user_name", username);
        User user1 = userServiceMapper.selectOne(wrapper);
        //The user already exists. Please log in directly
        if (user1 != null) {
            return UserRegisterVo.builder()
                    .info(ALREADY_EXISTS)
                    .build();
        }
        //Generate a four-digit verification code
        String code = smsService.creatCode();
        redisTemplate.opsForValue().set(KEY_R, code);
        //Send verification code
        boolean result = smsService.sendCode(userRegisterDTO.getPhone(), code);
        if (result) {
            return UserRegisterVo.builder()
                    .info(SMS_SEND_SUCCESSFULLY)
                    .code(code)
                    .build();
        } else {
            return UserRegisterVo.builder()
                    .info(SMS_SEND_FAILURE)
                    .build();
        }
//        //Check verification code
//        boolean flag = smsService.verifyCode(code,userRegisterDTO.getUserCode());
//        if (flag){
//            System.out.println("Verification code correct！");
//        }else {
//            return UserRegisterVo.builder()
//                    .info(NUMBER_CODE_NOT_EQUAL)
//                    .build();
//        }

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
        //Generate a four-digit verification code
        String code = smsService.creatCode();
        redisTemplate.opsForValue().set(KEY_F, code);
        //Send verification code
        boolean result = smsService.sendCode(phoneNumber, code);
        if (!result) {
            throw new Exception(MessageConstant.SMS_SEND_FAILURE);
        }
        user.setPassword(forgetDTO.getNewPassword());
        redisTemplate.opsForValue().set("phone", phoneNumber);
        redisTemplate.opsForValue().set("newPassword", forgetDTO.getNewPassword());
    }

    @Override
    public void updatePassword() {
        String newPassword = (String) redisTemplate.opsForValue().get("newPassword");
        String phoneNumber = (String) redisTemplate.opsForValue().get("phone");
        MPJQueryWrapper<User> wrapper = new MPJQueryWrapper<>();
        wrapper.selectAll(User.class)
                .eq("phone", phoneNumber);
        User user = userServiceMapper.selectOne(wrapper);
        user.setPassword(newPassword);
        user.setUpdateTime(LocalDateTime.now());
        userServiceMapper.updateById(user);
        redisTemplate.delete("phone");
        redisTemplate.delete("newPassword");
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
