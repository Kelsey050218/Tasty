package com.cs183.tasty.entity.DTO;

import com.cs183.tasty.entity.pojo.Recipe;
import lombok.Data;

@Data
public class NoteDTO {

    private String notePicture;

    private String describe;

    private Recipe recipe;
}
