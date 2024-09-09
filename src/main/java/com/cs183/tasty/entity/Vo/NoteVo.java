package com.cs183.tasty.entity.Vo;

import com.cs183.tasty.entity.pojo.Comment;
import com.cs183.tasty.entity.pojo.Recipe;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class NoteVo {
    private String notePicture;
    private String describe;
    private Recipe recipe;
    private LocalDateTime noteTime;
    private List<Comment> comments;
    private Long likeNum;
}
