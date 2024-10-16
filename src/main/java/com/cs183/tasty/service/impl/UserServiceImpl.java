package com.cs183.tasty.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs183.tasty.constant.MessageConstant;
import com.cs183.tasty.context.BaseContext;
import com.cs183.tasty.entity.DTO.ForgetDTO;
import com.cs183.tasty.entity.DTO.UserRegisterDTO;
import com.cs183.tasty.entity.pojo.*;
import com.cs183.tasty.mapper.FollowMapper;
import com.cs183.tasty.mapper.MenuMapper;
import com.cs183.tasty.mapper.MotivationMapper;
import com.cs183.tasty.mapper.UserServiceMapper;
import com.cs183.tasty.service.SmsService;
import com.cs183.tasty.service.UserService;
import com.github.yulichang.query.MPJQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static com.cs183.tasty.constant.MessageConstant.*;
import static com.cs183.tasty.constant.RedisConstants.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserServiceMapper, User> implements UserService {
    public static final String KEY_M = "Motivation";


    @Autowired
    private UserServiceMapper userServiceMapper;

    @Autowired
    private SmsService smsService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MotivationMapper motivationMapper;

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private FollowMapper followMapper;

    /**
     * 用户注册
     * @param userRegisterDTO
     * @throws Exception
     */
    @Override
    public void userRegister(UserRegisterDTO userRegisterDTO) throws Exception {
        //查询用户是否存在
        String username = userRegisterDTO.getUserName();
        MPJQueryWrapper<User> wrapper = new MPJQueryWrapper<>();
        wrapper.selectAll(User.class)
                .eq("user_name", username);
        User user = userServiceMapper.selectOne(wrapper);
        if (user != null) {
            throw new Exception(ALREADY_EXISTS);
        }
        //获取验证码
        String code = stringRedisTemplate.opsForValue().get(VERIFY_CODE);
        if(StrUtil.isBlank(code)){
            throw new Exception(NUMBER_CODE_EXPIRED);
        }
        //验证成功则新增用户
        if(userRegisterDTO.getCode().equals(code)){
            stringRedisTemplate.delete(VERIFY_CODE);
            User newUser = new User();
            BeanUtils.copyProperties(userRegisterDTO,newUser);
            String gensalt = BCrypt.gensalt();
            String saltPassword = BCrypt.hashpw(userRegisterDTO.getPassword(), gensalt);
            newUser.setPassword(saltPassword);
            newUser.setCreateTime(LocalDateTime.now());
            userServiceMapper.insert(newUser);
            menuMapper.bondUserRole(newUser.getUserId());
        }else{
            stringRedisTemplate.delete(VERIFY_CODE);
            throw new Exception(NUMBER_CODE_NOT_EQUAL);
        }
    }

    /**
     * 查询用户详情
     * @param id
     * @return
     */
    @Override
    public UserInfo getById(Long id) {
        User user = userServiceMapper.selectById(id);
        return UserInfo.builder()
                .username(user.getUserName())
                .sex(user.getSex())
                .place(user.getPlace())
                .resume(user.getResume())
                .portrait(user.getPortrait())
                .build();
    }

    /**
     * 更新用户信息
     * @param userInfo
     */
    @Override
    public void updateInfo(UserInfo userInfo) {
        String userName = userInfo.getUsername();
        User user = userServiceMapper.selectOne(Wrappers.lambdaQuery(User.class).eq(User::getUserName, userName));
        BeanUtils.copyProperties(userInfo, user);
        user.setUpdateTime(LocalDateTime.now());
        userServiceMapper.updateById(user);
    }

    /**
     * 忘记密码
     * @param forgetDTO
     * @throws Exception
     */
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
        String code = stringRedisTemplate.opsForValue().get(VERIFY_CODE);
        if(StrUtil.isBlank(code)){
            throw new Exception(NUMBER_CODE_EXPIRED);
        }
        if(forgetDTO.getCode().equals(code)){
            stringRedisTemplate.delete(VERIFY_CODE);
            user.setPassword(forgetDTO.getNewPassword());
            userServiceMapper.updateById(user);
        }else{
            stringRedisTemplate.delete(VERIFY_CODE);
            throw new Exception(NUMBER_CODE_NOT_EQUAL);
        }
    }

    /**
     * 获取每日一句
     * @return 每日一句
     */
    @Override
    public String getSentence() {
        boolean flag = Boolean.TRUE.equals(stringRedisTemplate.hasKey(KEY_M));
        if (flag) {
            String sentence = stringRedisTemplate.opsForValue().get(KEY_M);
            sentence = sentence.replaceAll("\\u0000", "");
            return sentence;
        } else {
            Random random = new Random();
            int randomNumber = random.nextInt(1, 11);
            Motivation motivation = motivationMapper.selectById(randomNumber);
            stringRedisTemplate.opsForValue().set(KEY_M, motivation.getSentence(), 60 * 60 * 24);
            return motivation.getSentence();
        }
    }

    /**
     * 关注和取关
     * @param followUserId
     * @param isFollow
     */
    @Override
    public void follow(Long followUserId, boolean isFollow) throws Exception {
        Long userId = BaseContext.getCurrentId();
        String followKey = FOLLOW_USER + userId;
        String fansKey = FANS_USER + followUserId;
        if (isFollow) {
            //先操作数据库
            Follow follow = new Follow();
            follow.setUserId(userId);
            follow.setFollowUserId(followUserId);
            int insert = followMapper.insert(follow);
            //插入关注关系成功
            if(insert == 1){
                //再删除缓存
                stringRedisTemplate.delete(followKey);
                stringRedisTemplate.delete(fansKey);
            }else{
                throw new Exception(REDIS_ERROR);
            }
        } else {
            //取关
            int delete = followMapper.delete(new QueryWrapper<Follow>()
                    .eq("user_id", userId)
                    .eq("follow_user_id", followUserId));
            if(delete == 1){
                //删除redis对应set
                stringRedisTemplate.delete(followKey);
                stringRedisTemplate.delete(fansKey);
            }else{
                throw new Exception(REDIS_ERROR);
            }

        }
    }

    /**
     * 获取粉丝列表
     * @param id
     * @return 用户列表
     */
    @Override
    public List<User> getFans(Long id) {
        String key = FANS_USER + id;
        //先从缓存中获取
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            return getUserList(key);
        }else{
            //如果缓存里没有就更新缓存
            //根据用户id获取粉丝列表
            List<Follow> fansList = followMapper.selectList(Wrappers.lambdaQuery(Follow.class)
                    .eq(Follow::getFollowUserId, id));
            List<Long> fanIds = fansList.stream()
                    .map(Follow::getUserId)
                    .toList();
            List<User> userList = userServiceMapper.selectBatchIds(fanIds);
            // 更新缓存
            for (Long fanId : fanIds) {
                stringRedisTemplate.opsForSet().add(key, fanId.toString());
            }
//            for (Follow follow : fansList) {
//                User user = userServiceMapper.selectById(follow.getUserId());
//                userList.add(user);
//                //更新缓存
//                stringRedisTemplate.opsForSet().add(key,follow.getUserId().toString());
//            }
            return userList;
        }
    }

    /**
     * 获取关注列表
     * @param id
     * @return 用户列表
     */
    @Override
    public List<User> getFollowList(Long id) {
        String key = FOLLOW_USER + id;
        //先从缓存中获取
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(key))) {
            return getUserList(key);
        }else{
            //如果缓存里没有就更新缓存
            //根据用户id获取关注列表
            List<Follow> followList = followMapper.selectList(Wrappers.lambdaQuery(Follow.class)
                    .eq(Follow::getUserId, id));
            List<Long> followIds = followList.stream()
                    .map(Follow::getUserId)
                    .toList();
            // 批量查询用户信息
            List<User> userList = userServiceMapper.selectBatchIds(followIds);
            // 更新缓存
            for (Long followId : followIds) {
                stringRedisTemplate.opsForSet().add(key, followId.toString());
            }
            return userList;
        }
    }

    /**
     * 获取互关好友
     * @return 用户列表
     */
    @Override
    public List<User> getMutualFans() {
        //当前用户id
        Long id = BaseContext.getCurrentId();
        String followKey = FOLLOW_USER + id;
        String fansKey = FANS_USER + id;
        boolean fan = Boolean.TRUE.equals(stringRedisTemplate.hasKey(fansKey));
        boolean follow = Boolean.TRUE.equals(stringRedisTemplate.hasKey(followKey));
        //先判断有没有key
        if(!((fan) && (follow))){
            //没有就更新缓存
            getFans(id);
            getFollowList(id);
        }
        //用set的intersect求交集
        Set<String> intersect = stringRedisTemplate.opsForSet().intersect(fansKey, followKey);
        if (intersect == null || intersect.isEmpty()) {
            // 无交集
            return Collections.emptyList();
        }
        //解析id集合
        List<Long> ids = intersect.stream().map(Long::valueOf).toList();
        return userServiceMapper.selectBatchIds(ids);
    }

    /**
     * 动态条件查询用户
     * @param username
     * @param phone
     * @return 用户列表
     */
    @Override
    public List<User> conditionSearch(String username, String phone) {
        String key = SEARCH_RECORD;
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 模糊查询用户名
        if (username != null && !username.isEmpty()) {
            //搜索记录存入redis:zset
            stringRedisTemplate.opsForZSet().add(key,username,System.currentTimeMillis());
            queryWrapper.like("username", username);
        }

        // 查询手机号
        if (phone != null && !phone.isEmpty()) {
            //搜索记录存入redis:zset
            stringRedisTemplate.opsForZSet().add(key,phone,System.currentTimeMillis());
            queryWrapper.eq("phone", phone);
        }
        return userServiceMapper.selectList(queryWrapper);
    }


    //从redis的set集合中获取用户list
    private List<User> getUserList(String key){
        Set<String> set = stringRedisTemplate.opsForSet().members(key);
        List<String> list = set != null ? set.stream().toList() : List.of();
        return userServiceMapper.selectBatchIds(list);
    }
}
