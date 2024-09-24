package com.cs183.tasty.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cs183.tasty.entity.pojo.LoginUser;
import com.cs183.tasty.entity.pojo.User;
import com.cs183.tasty.mapper.MenuMapper;
import com.cs183.tasty.mapper.UserServiceMapper;
import com.sun.xml.bind.v2.TODO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Objects;

public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserServiceMapper userServiceMapper;
    @Autowired
    MenuMapper menuMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //查询数据库中此用户
        LambdaQueryWrapper wrapper = new LambdaQueryWrapper<User>().eq(User::getUserName, username);
        User user = userServiceMapper.selectOne(wrapper);

        //判断是否存在
        if(Objects.isNull(user)){
            //不存在，则抛出异常
            throw new UsernameNotFoundException("该用户不存在，请注册");
        }
        //若存在，则查询后封装用户信息和权限信息
        List<String> list = menuMapper.selectPermsByUserID(user.getUserId());


        //返回LoginUser（UserDetail的实现类）
        return new LoginUser(user,list);

    }
}
