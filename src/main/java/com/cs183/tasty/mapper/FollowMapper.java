package com.cs183.tasty.mapper;

import com.cs183.tasty.entity.pojo.Comment;
import com.cs183.tasty.entity.pojo.Follow;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FollowMapper extends MPJBaseMapper<Follow> {

}
