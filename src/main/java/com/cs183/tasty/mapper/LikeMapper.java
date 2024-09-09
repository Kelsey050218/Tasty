package com.cs183.tasty.mapper;

import com.cs183.tasty.entity.pojo.Like;
import com.cs183.tasty.entity.pojo.Motivation;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LikeMapper extends MPJBaseMapper<Like> {
}
