package com.cs183.tasty.entity.Vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.cs183.tasty.entity.pojo.Guide;
import com.cs183.tasty.entity.pojo.Ingredient;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeVo implements Serializable {

    private Long recipeId;

    private String menu;

    private String type;

    private Integer people;

    private int time;

    private int calories;

    private String recipePicture;

    private Integer score;

    private String name;

    private Guide guide;

    private List<Ingredient> ingredients;

}
