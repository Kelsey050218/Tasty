package com.cs183.tasty.service.impl;

import com.cs183.tasty.common.PageResult;
import com.cs183.tasty.context.BaseContext;
import com.cs183.tasty.entity.DTO.RecipeDTO;
import com.cs183.tasty.entity.Vo.RecipeVo;
import com.cs183.tasty.entity.pojo.Collect;
import com.cs183.tasty.entity.pojo.Guide;
import com.cs183.tasty.entity.pojo.Ingredient;
import com.cs183.tasty.entity.pojo.Recipe;
import com.cs183.tasty.mapper.CollectMapper;
import com.cs183.tasty.mapper.GuideMapper;
import com.cs183.tasty.mapper.IngredientMapper;
import com.cs183.tasty.mapper.RecipeMapper;
import com.cs183.tasty.service.RecipeService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.yulichang.query.MPJQueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RecipeServiceImpl implements RecipeService {

    @Autowired
    private RecipeMapper recipeMapper;

    @Autowired
    private GuideMapper guideMapper;

    @Autowired
    private IngredientMapper ingredientMapper;

    @Autowired
    private CollectMapper collectMapper;

    @Override
    public RecipeVo search(RecipeDTO recipeDTO) {
        MPJQueryWrapper<Recipe> wrapper = new MPJQueryWrapper<>();
        wrapper.selectAll(Recipe.class);

        if (recipeDTO.getMenu() != null) {
            wrapper.eq("menu", recipeDTO.getMenu());
        }
        if (recipeDTO.getType() != null) {
            wrapper.eq("type", recipeDTO.getType());
        }
        if (recipeDTO.getPeople() != null) {
            wrapper.eq("people", recipeDTO.getPeople());
        }
        if (Objects.nonNull(recipeDTO.getTime()) && recipeDTO.getTime() != 0) {
            wrapper.eq("time", recipeDTO.getTime());
        }
        if (Objects.nonNull(recipeDTO.getCalories()) && recipeDTO.getCalories() != 0) {
            wrapper.eq("calories", recipeDTO.getCalories());
        }
        Recipe recipe = recipeMapper.selectOne(wrapper);

        Guide guide = guideMapper.selectById(recipe.getRecipeId());

        MPJQueryWrapper<Ingredient> w = new MPJQueryWrapper<>();
        w.selectAll(Ingredient.class);
        w.eq("recipe_id", recipe.getRecipeId());
        List<Ingredient> ingredients = ingredientMapper.selectList(w);
        RecipeVo recipeVo = new RecipeVo();
        BeanUtils.copyProperties(recipe, recipeVo);
        recipeVo.setGuide(guide);
        recipeVo.setIngredients(ingredients);
        return recipeVo;
    }

    @Override
    public List<Recipe> recommend() {
        MPJQueryWrapper<Recipe> wrapper = new MPJQueryWrapper<>();
        wrapper.selectAll(Recipe.class);
        List<Recipe> allRecipes = recipeMapper.selectList(wrapper);
        List<Recipe> recommendRecipes = new ArrayList<>();
        Set<Integer> numbers = new HashSet<>();
        Random random = new Random();
        while (numbers.size() < 5) {
            int randomNumber = random.nextInt(allRecipes.size() + 1);
            numbers.add(randomNumber);
        }
        for (int index : numbers) {
            recommendRecipes.add(allRecipes.get(index));
        }
        return recommendRecipes;
    }

    @Override
    public RecipeVo getById(Long id) {
        Recipe recipe = recipeMapper.selectById(id);
        Guide guide = guideMapper.selectById(recipe.getRecipeId());
        MPJQueryWrapper<Ingredient> w = new MPJQueryWrapper<>();
        w.selectAll(Ingredient.class);
        w.eq("recipe_id", recipe.getRecipeId());
        List<Ingredient> ingredients = ingredientMapper.selectList(w);
        RecipeVo recipeVo = new RecipeVo();
        BeanUtils.copyProperties(recipe, recipeVo);
        recipeVo.setGuide(guide);
        recipeVo.setIngredients(ingredients);
        return recipeVo;
    }

    @Override
    public void collect(Long id) {
        Collect collect = new Collect();
        collect.setRecipeId(id);
        collect.setUserId(BaseContext.getCurrentId());
        collectMapper.insert(collect);
    }

    @Override
    public List<Recipe> getCollect() {
        MPJQueryWrapper<Collect> wrapper = new MPJQueryWrapper<>();
        wrapper.selectAll(Collect.class);
        wrapper.eq("user_id", BaseContext.getCurrentId());
        List<Collect> collects = collectMapper.selectList(wrapper);
        List<Long> recipeIds = new ArrayList<>();
        for (Collect collect : collects) {
            recipeIds.add(collect.getRecipeId());
        }
        MPJQueryWrapper<Recipe> recipeWrapper = new MPJQueryWrapper<>();
        recipeWrapper.selectAll(Recipe.class);
        recipeWrapper.in("recipe_id", recipeIds);
        return recipeMapper.selectList(recipeWrapper);
    }
}

