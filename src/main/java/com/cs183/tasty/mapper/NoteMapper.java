package com.cs183.tasty.mapper;

import com.cs183.tasty.entity.DTO.PageQueryDTO;
import com.cs183.tasty.entity.pojo.Motivation;
import com.cs183.tasty.entity.pojo.Note;
import com.github.pagehelper.Page;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoteMapper extends MPJBaseMapper<Note> {
    Page<Note> pageQuery(PageQueryDTO pageQueryDTO);
}
