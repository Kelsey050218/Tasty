package com.cs183.tasty.controller;

import com.cs183.tasty.common.PageResult;
import com.cs183.tasty.common.Result;
import com.cs183.tasty.entity.DTO.*;
import com.cs183.tasty.entity.Vo.NoteVo;
import com.cs183.tasty.entity.pojo.Comment;
import com.cs183.tasty.entity.pojo.Note;
import com.cs183.tasty.service.NoteService;
import com.sun.xml.bind.v2.TODO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/note")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<String> addNote(@RequestBody NoteDTO noteDTO) {
        noteService.addNote(noteDTO);
        return Result.ok();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<String> delete(@RequestParam Long id) throws Exception {
        noteService.delete(id);
        return Result.ok();
    }

    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public Result<PageResult> NotePageQuery(PageQueryDTO pageQueryDTO) {
        PageResult pageResult = noteService.NotePageQuery(pageQueryDTO);
        return Result.ok(pageResult);
    }

    @RequestMapping(value = "/myNotes", method = RequestMethod.GET)
    public Result<PageResult> myNotes(PageQueryDTO pageQueryDTO) {
        PageResult pageResult = noteService.getMyNotes(pageQueryDTO);
        return Result.ok(pageResult);
    }

    @RequestMapping(value = "/getNoteById", method = RequestMethod.GET)
    public Result<NoteVo> getNoteById(@RequestParam Long id) {
        NoteVo noteVo = noteService.getNoteById(id);
        return Result.ok(noteVo);
    }

    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public Result<String> comment(@RequestBody CommentDTO commentDTO) {
        noteService.comment(commentDTO);
        return Result.ok();
    }

    @RequestMapping(value = "/like", method = RequestMethod.POST)
    public Result<String> like(@RequestParam Long id) {
        noteService.like(id);
        return Result.ok();
    }

    @RequestMapping(value = "/getLike", method = RequestMethod.GET)
    public Result<List<Note>> getLike() {
        List<Note> likeNotes = noteService.getLike();
        return Result.ok(likeNotes);
    }

    @RequestMapping(value = "/report", method = RequestMethod.POST)
    public Result<Object> reportNote(@RequestBody ReportDTO reportDTO){
        noteService.report(reportDTO);
        return Result.ok();
    }

}
