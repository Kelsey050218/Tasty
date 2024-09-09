package com.cs183.tasty.entity.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Note {
    @TableId(value = "note_id", type = IdType.AUTO)
    private Long noteId;

    @TableField(value = "noteuser_id")
    private Long noteUserId;

    @TableField(value = "note_picture")
    private String notePicture;

    @TableField(value = "describe")
    private String describe;

    @TableField(value = "recipe_id")
    private Long recipeId;

    @TableField(value = "note_time")
    private LocalDateTime noteTime;
}
