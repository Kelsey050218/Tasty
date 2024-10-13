package com.cs183.tasty.entity.DTO;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RecipeDTO {

    @JsonProperty("name")
    private String name;
    @JsonProperty("menu")
    private String menu;
    @JsonProperty("type")
    private String type;
    @JsonProperty("people")
    private Integer people;
    @JsonProperty("time")
    private int time;
    @JsonProperty("calories")
    private int calories;
    @JsonProperty("recipe_picture")
    private String recipePicture;

}
