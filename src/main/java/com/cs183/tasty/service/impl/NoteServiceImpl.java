package com.cs183.tasty.service.impl;

import com.cs183.tasty.common.PageResult;
import com.cs183.tasty.context.BaseContext;
import com.cs183.tasty.entity.DTO.CommentDTO;
import com.cs183.tasty.entity.DTO.NoteDTO;
import com.cs183.tasty.entity.DTO.PageQueryDTO;
import com.cs183.tasty.entity.DTO.ReportDTO;
import com.cs183.tasty.entity.Vo.NoteVo;
import com.cs183.tasty.entity.pojo.*;
import com.cs183.tasty.mapper.*;
import com.cs183.tasty.service.NoteService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.yulichang.query.MPJQueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.cs183.tasty.constant.MessageConstant.NO_PERMISSION;

@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteMapper noteMapper;

    @Autowired
    private RecipeMapper recipeMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private LikeMapper likeMapper;

    @Autowired
    private ReportMapper reportMapper;



    @Override
    public void addNote(NoteDTO noteDTO) {
        Note note = new Note();
        note.setNotePicture(noteDTO.getNotePicture());
        note.setDescribe(noteDTO.getDescribe());
        note.setNoteTime(LocalDateTime.now());
        note.setNoteUserId(BaseContext.getCurrentId());
        note.setRecipeId(noteDTO.getRecipe().getRecipeId());
        noteMapper.insert(note);
    }

    @Override
    public void delete(Long id) throws Exception {
        Long userId = noteMapper.getUserId(id);
        if(!Objects.equals(userId, BaseContext.getCurrentId())){
            throw new Exception(NO_PERMISSION);
        }
        noteMapper.deleteById(id);
    }

    @Override
    public PageResult NotePageQuery(PageQueryDTO pageQueryDTO) {
        PageHelper.startPage(pageQueryDTO.getPage(),pageQueryDTO.getPageSize());
        Page<Note> page = noteMapper.pageQuery(pageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public NoteVo getNoteById(Long id) {
        NoteVo noteVo = new NoteVo();
        Note note = noteMapper.selectById(id);
        noteVo.setNotePicture(note.getNotePicture());
        noteVo.setDescribe(note.getDescribe());
        noteVo.setNoteTime(note.getNoteTime());
        Recipe recipe = recipeMapper.selectById(note.getRecipeId());
        noteVo.setRecipe(recipe);
        MPJQueryWrapper<Comment> wrapper = new MPJQueryWrapper<>();
        wrapper.selectAll(Comment.class);
        wrapper.eq("note_id", id);
        List<Comment> comments = commentMapper.selectList(wrapper);
        noteVo.setComments(comments);
        MPJQueryWrapper<Like> likeWrapper = new MPJQueryWrapper<>();
        likeWrapper.selectAll(Like.class);
        likeWrapper.eq("note_id", id);
        Long likeNum =likeMapper.selectCount(likeWrapper);
        noteVo.setLikeNum(likeNum);
        return noteVo;
    }

    @Override
    public PageResult getMyNotes(PageQueryDTO pageQueryDTO) {
        pageQueryDTO.setUserId(BaseContext.getCurrentId());
        PageHelper.startPage(pageQueryDTO.getPage(),pageQueryDTO.getPageSize());
        Page<Note> page = noteMapper.pageQuery(pageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void comment(CommentDTO commentDTO) {
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentDTO, comment);
        comment.setCommentTime(LocalDateTime.now());
        comment.setUserId(BaseContext.getCurrentId());
        commentMapper.insert(comment);
    }

    @Override
    public void like(Long id) {
        Like like = new Like();
        like.setNoteUserId(id);
        like.setUserId(BaseContext.getCurrentId());
        likeMapper.insert(like);
    }

    @Override
    public List<Note> getLike() {
        MPJQueryWrapper<Like> wrapper = new MPJQueryWrapper<>();
        wrapper.selectAll(Like.class);
        wrapper.eq("user_id", BaseContext.getCurrentId());
        List<Like> likes = likeMapper.selectList(wrapper);
        List<Long> NoteIds = new ArrayList<>();
        for (Like like : likes) {
            NoteIds.add(like.getNoteUserId());
        }
        MPJQueryWrapper<Note> NoteWrapper = new MPJQueryWrapper<>();
        NoteWrapper.selectAll(Note.class);
        NoteWrapper.in("note_id", NoteIds);
        return noteMapper.selectList(NoteWrapper);
    }

    @Override
    public void report(ReportDTO reportDTO) {
        Report report = new Report();
        BeanUtils.copyProperties(reportDTO,report);
        report.setReportStatus(0);
        report.setReportUserId(BaseContext.getCurrentId());
        report.setCreatTime(LocalDateTime.now());
//        reportMapper.addReport(report);
        reportMapper.insert(report);
    }
}
