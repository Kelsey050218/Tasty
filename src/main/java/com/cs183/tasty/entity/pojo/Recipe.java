package com.cs183.tasty.entity.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import javax.management.ValueExp;
import java.io.Serializable;

@Data
@TableName(value ="recipe")
@Builder
public class Recipe implements Serializable {

    @TableId(value = "recipe_id",type = IdType.AUTO)
    private Long recipeId;

    @TableField(value = "menu")
    private String menu;

    @TableField(value = "type")
    private String type;

    @TableField(value = "people")
    private Integer people;

    @TableField(value = "time")
    private int time;

    @TableField(value = "calories")
    private int calories;

    @TableField(value = "recipe_picture")
    private String recipePicture;

    @TableField(value = "score")
    private Integer score;

    @TableField(value = "name")
    private String name;

}
