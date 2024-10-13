package com.cs183.tasty.mapper;

import com.cs183.tasty.entity.pojo.Guide;
import com.cs183.tasty.entity.pojo.Ingredient;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IngredientMapper extends MPJBaseMapper<Ingredient> {
}
