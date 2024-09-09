package com.cs183.tasty.mapper;

import com.cs183.tasty.entity.pojo.Recipe;
import com.cs183.tasty.entity.pojo.User;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RecipeMapper extends MPJBaseMapper<Recipe> {

}
