package com.cs183.tasty.mapper;

import com.cs183.tasty.entity.DTO.PageQueryDTO;
import com.cs183.tasty.entity.pojo.Motivation;
import com.cs183.tasty.entity.pojo.Note;
import com.cs183.tasty.entity.pojo.Report;
import com.github.pagehelper.Page;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

@Mapper
public interface NoteMapper extends MPJBaseMapper<Note> {
    Page<Note> pageQuery(PageQueryDTO pageQueryDTO);


    @Select("select noteuser_id from note where note_id = #{id}")
    Long getUserId(Long id);
}
