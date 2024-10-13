package com.cs183.tasty.entity.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Guide implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("recipe_id")
    private Long recipeId;

    @TableField("steps")
    private String steps;


}
