package com.cs183.tasty.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MenuMapper {

    List<String> selectPermsByUserID(Long userId);

    @Insert("insert into user_role (user_id, role_id) values (#{userId}, (select id from role where role_key = 'user'))")
    void bondRole(Long userId);
}
