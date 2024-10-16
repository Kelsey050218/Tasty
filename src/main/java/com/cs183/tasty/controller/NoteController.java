package com.cs183.tasty.controller;

import com.cs183.tasty.common.PageResult;
import com.cs183.tasty.common.Result;
import com.cs183.tasty.entity.DTO.*;
import com.cs183.tasty.entity.Vo.NoteVo;
import com.cs183.tasty.entity.pojo.Comment;
import com.cs183.tasty.entity.pojo.Note;
import com.cs183.tasty.entity.pojo.Response;
import com.cs183.tasty.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/note")
public class NoteController {

    @Autowired
    private NoteService noteService;

    //新增笔记
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<String> addNote(@RequestBody NoteDTO noteDTO) {
        noteService.addNote(noteDTO);
        return Result.ok();
    }

    //删除自己的笔记
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<String> delete(@RequestParam Long id) throws Exception {
        noteService.delete(id);
        return Result.ok();
    }

    //分页查询笔记
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public Result<PageResult> NotePageQuery(PageQueryDTO pageQueryDTO) {
        PageResult pageResult = noteService.NotePageQuery(pageQueryDTO);
        return Result.ok(pageResult);
    }

    //查询我的笔记
    @RequestMapping(value = "/myNotes", method = RequestMethod.GET)
    public Result<PageResult> myNotes(PageQueryDTO pageQueryDTO) {
        PageResult pageResult = noteService.getMyNotes(pageQueryDTO);
        return Result.ok(pageResult);
    }

    //笔记点击量排行榜
    @RequestMapping(value = "/click/rank",method = RequestMethod.GET)
    public Result<List<Note>> noteRank(){
        List<Note> notes = noteService.rank();
        return Result.ok(notes);
    }

    //查看笔记详情
    @RequestMapping(value = "/getNote/{id}", method = RequestMethod.GET)
    public Result<NoteVo> getNoteById(@PathVariable Long id) {
        NoteVo noteVo = noteService.getNote(id);
        return Result.ok(noteVo);
    }

    //评论笔记
    @RequestMapping(value = "/comment/{id}", method = RequestMethod.POST)
    public Result<String> comment(@RequestParam String comment,@PathVariable Long id) {
        noteService.comment(comment,id);
        return Result.ok();
    }

    //点赞笔记
    @RequestMapping(value = "/like", method = RequestMethod.POST)
    public Result<String> like(@RequestParam Long id) {
        noteService.like(id);
        return Result.ok();
    }

    //获取用户自己点赞过的笔记
    @RequestMapping(value = "/getLike", method = RequestMethod.GET)
    public Result<List<Note>> getLike() {
        List<Note> likeNotes = noteService.getLike();
        return Result.ok(likeNotes);
    }

    //举报违规笔记
    @RequestMapping(value = "/report", method = RequestMethod.POST)
    public Result<Object> reportNote(@RequestBody ReportDTO reportDTO){
        noteService.report(reportDTO);
        return Result.ok();
    }

    //搜索笔记（动态搜索）
    @RequestMapping(value = "/search")
    public Result<List<Note>> searchNote(@RequestParam(required = false) String describe,
                                         @RequestParam(required = false) int year){
        List<Note> noteList = noteService.conditionSearch(describe, year);
        return Result.ok(noteList);
    }

    //回复评论
    @RequestMapping(value = "/comment/response/{id}")
    public Result<Object> commentResponse(@PathVariable Long id, @RequestParam String response){
        noteService.addResponse(id,response);
        return Result.ok();
    }

    //查看评论详情
    @RequestMapping(value = "/comment/details/{id}")
    public Result<List<Response>> getCommentById(@PathVariable Long id){
        List<Response> responseList = noteService.getResponses(id);
        return Result.ok(responseList);
    }

    //删除评论
    @RequestMapping(value = "/comment/delete/{id}")
    public Result<Object> deleteComment(@PathVariable Long id){
        noteService.deleteComment(id);
        return Result.ok();
    }


}
