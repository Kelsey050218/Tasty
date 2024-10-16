package com.cs183.tasty.service;

import com.cs183.tasty.common.PageResult;
import com.cs183.tasty.entity.DTO.NoteDTO;
import com.cs183.tasty.entity.DTO.PageQueryDTO;
import com.cs183.tasty.entity.DTO.ReportDTO;
import com.cs183.tasty.entity.Vo.NoteVo;
import com.cs183.tasty.entity.pojo.Note;
import com.cs183.tasty.entity.pojo.Comment;
import com.cs183.tasty.entity.pojo.Recipe;
import com.cs183.tasty.entity.pojo.Response;

import java.util.List;

public interface NoteService {

    void addNote(NoteDTO noteDTO);

    void delete(Long id) throws Exception;

    PageResult NotePageQuery(PageQueryDTO pageQueryDTO);

    NoteVo getNote(Long id);

    PageResult getMyNotes(PageQueryDTO pageQueryDTO);

    void comment(String comment,Long id);

    void like(Long id);

    List<Note> getLike();

    void report(ReportDTO reportDTO);

    List<Note> rank();

    List<Note> conditionSearch(String describe, int year);

    void addResponse(Long id, String response);

    List<Response> getResponses(Long id);

    void deleteComment(Long id);
}
