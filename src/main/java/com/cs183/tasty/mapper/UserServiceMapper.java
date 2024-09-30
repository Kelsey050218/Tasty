package com.cs183.tasty.mapper;

import com.cs183.tasty.entity.pojo.Report;
import com.cs183.tasty.entity.pojo.UserInfo;
import com.github.yulichang.base.MPJBaseMapper;
import com.cs183.tasty.entity.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserServiceMapper extends MPJBaseMapper<User> {
//    @Select("select * from user where user_name = #{username}")
//    User getByUserName(String username);
//    @Insert("insert into `ry-vue`.user "+
//            "(user_id, user_name, phone, sex, create_time,status,password) " +
//            "VALUES "+
//            "(#{userId}, #{userName}, #{phone}, #{sex}, #{createTime},#{status},#{password})")
//    void save(User user);
}


