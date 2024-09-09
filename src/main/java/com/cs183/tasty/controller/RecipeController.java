package com.cs183.tasty.controller;

import com.cs183.tasty.common.PageResult;
import com.cs183.tasty.common.Result;
import com.cs183.tasty.constant.MessageConstant;
import com.cs183.tasty.context.BaseContext;
import com.cs183.tasty.entity.DTO.RecipeDTO;
import com.cs183.tasty.entity.Vo.RecipeVo;
import com.cs183.tasty.entity.pojo.Recipe;
import com.cs183.tasty.service.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recipe")
@Slf4j
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result<RecipeVo> search(@RequestBody RecipeDTO recipeDTO) {
        RecipeVo recipeVo = recipeService.search(recipeDTO);
        if (recipeVo == null) {
            return Result.fail("No recipe found");
        } else {
            return Result.ok(recipeVo);
        }
    }

    @RequestMapping(value = "/recommend", method = RequestMethod.GET)
    public Result<List<Recipe>> recommend() {
        List<Recipe> list = recipeService.recommend();
        if (list == null) {
            return Result.fail(MessageConstant.NO_RECIPES_FOUND);
        } else {
            return Result.ok(list);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result<RecipeVo> getById(@PathVariable("id") Long id) {
        RecipeVo recipeVo = recipeService.getById(id);
        return Result.ok(recipeVo);
    }

    @RequestMapping(value = "/collect",method = RequestMethod.POST)
    public Result<String> collect(Long id) {
        recipeService.collect(id);
        return Result.ok();
    }

    @RequestMapping(value = "/getCollect",method = RequestMethod.GET)
    public Result<List<Recipe>> getCollect() {
        List<Recipe> list = recipeService.getCollect();
        return Result.ok(list);
    }
}

