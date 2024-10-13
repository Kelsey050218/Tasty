package com.cs183.tasty.service;

import com.cs183.tasty.common.PageResult;
import com.cs183.tasty.entity.DTO.RecipeDTO;
import com.cs183.tasty.entity.Vo.RecipeVo;
import com.cs183.tasty.entity.pojo.Recipe;

import java.util.List;

public interface RecipeService {
    RecipeVo search(RecipeDTO recipeDTO);

    List<Recipe> recommend();

    RecipeVo getById(Long id);

    void collect(Long id);

    List<Recipe> getCollect();
}
