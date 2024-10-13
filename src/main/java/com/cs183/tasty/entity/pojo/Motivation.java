package com.cs183.tasty.entity.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Motivation implements Serializable {


    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "sentence")
    private String sentence;
}
