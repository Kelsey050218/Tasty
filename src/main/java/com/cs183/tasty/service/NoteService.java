package com.cs183.tasty.service;

import com.cs183.tasty.common.PageResult;
import com.cs183.tasty.entity.DTO.CommentDTO;
import com.cs183.tasty.entity.DTO.NoteDTO;
import com.cs183.tasty.entity.DTO.PageQueryDTO;
import com.cs183.tasty.entity.DTO.ReportDTO;
import com.cs183.tasty.entity.Vo.NoteVo;
import com.cs183.tasty.entity.pojo.Comment;
import com.cs183.tasty.entity.pojo.Note;

import java.util.List;

public interface NoteService {
    void addNote(NoteDTO noteDTO);

    void delete(Long id) throws Exception;

    PageResult NotePageQuery(PageQueryDTO pageQueryDTO);

    NoteVo getNoteById(Long id);

    PageResult getMyNotes(PageQueryDTO pageQueryDTO);

    void comment(CommentDTO commentDTO);

    void like(Long id);

    List<Note> getLike();

    void report(ReportDTO reportDTO);
}
