package com.cs183.tasty.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cs183.tasty.common.PageResult;
import com.cs183.tasty.context.BaseContext;
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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

import static com.cs183.tasty.constant.MessageConstant.NO_PERMISSION;
import static com.cs183.tasty.constant.RedisConstants.*;

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

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 新增笔记
     * @param noteDTO
     */
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

    /**
     * 删除笔记
     * @param id
     * @throws Exception
     */
    @Override
    public void delete(Long id) throws Exception {
        Long userId = noteMapper.getUserId(id);
        if(!Objects.equals(userId, BaseContext.getCurrentId())){
            throw new Exception(NO_PERMISSION);
        }
        noteMapper.deleteById(id);
        //删除关联的笔记点赞记录
        likeMapper.delete(new QueryWrapper<Like>()
                .eq("noteuser_id",id));
        //删除关联的笔记评论记录
        commentMapper.delete(new QueryWrapper<Comment>()
                .eq("note_id",id));
        //删除笔记的点击量记录
        stringRedisTemplate.opsForZSet().remove(CLICK_NOTE,id);
    }

    /**
     * 分页查询笔记
     * @param pageQueryDTO
     * @return 分页笔记
     */
    @Override
    public PageResult NotePageQuery(PageQueryDTO pageQueryDTO) {
        PageHelper.startPage(pageQueryDTO.getPage(),pageQueryDTO.getPageSize());
        Page<Note> page = noteMapper.pageQuery(pageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 获取笔记详情
     * @param id
     * @return 笔记信息
     */
    @Override
    public NoteVo getNote(Long id) {
        //点击量缓存更新，该笔记点击数量+1
        stringRedisTemplate.opsForZSet().incrementScore(CLICK_NOTE,id.toString(),1);
        //数据库查询笔记主体部分
        NoteVo noteVo = new NoteVo();
        Note note = noteMapper.selectById(id);
        noteVo.setNotePicture(note.getNotePicture());
        noteVo.setDescribe(note.getDescribe());
        noteVo.setNoteTime(note.getNoteTime());
        Recipe recipe = recipeMapper.selectById(note.getRecipeId());
        noteVo.setRecipe(recipe);
        //缓存查询
        String likeKey = LIKE_NOTE + id;
        String commentKey = COMMENT_NOTE + id;


        //在缓存里面查询点赞数量
        if(!Boolean.TRUE.equals(stringRedisTemplate.hasKey(likeKey))){
            //如果没有点赞
            if(stringRedisTemplate.opsForValue().get(likeKey) == null){
                noteVo.setLikeNum(0);
            }else {
                Integer likeNum = Integer.valueOf(Objects.requireNonNull(stringRedisTemplate.opsForValue().get(likeKey)));
                noteVo.setLikeNum(likeNum);
            }
        }else{
            //数据库查询点赞数量
            MPJQueryWrapper<Like> likeWrapper = new MPJQueryWrapper<>();
            likeWrapper.selectAll(Like.class);
            likeWrapper.eq("note_id", id);
            Integer likeNum = Math.toIntExact(likeMapper.selectCount(likeWrapper));
            noteVo.setLikeNum(likeNum);
            //更新缓存
            stringRedisTemplate.opsForValue().set(likeKey, likeNum.toString());
        }


        //在缓存里面查询评论
        if(!Boolean.TRUE.equals(stringRedisTemplate.hasKey(commentKey))){
            Map<Object, Object> commentMap = stringRedisTemplate.opsForHash().entries(commentKey);
            List<Comment> comments = new ArrayList<>();
            for (Map.Entry<Object, Object> entry : commentMap.entrySet()) {
                Comment comment = new Comment();
                comment.setUserId(Long.valueOf((String) entry.getKey())); // userId
                comment.setRemark((String) entry.getValue()); // remark
                comments.add(comment);
            }
            noteVo.setComments(comments);
        }else{
            //数据库查询评论信息
            MPJQueryWrapper<Comment> wrapper = new MPJQueryWrapper<>();
            wrapper.selectAll(Comment.class);
            wrapper.eq("note_id", id);
            List<Comment> comments = commentMapper.selectList(wrapper);
            noteVo.setComments(comments);
            //更新缓存
            //评论者id做key，评论内容做value，用hash结构
            Map<Long, String> commentMap = new HashMap<>();
            // 将每个评论的 userId 和 remark 放入 Map
            for (Comment comment : comments) {
                commentMap.put(comment.getUserId(), comment.getRemark());
            }
            // 将 Map 存储到 Redis Hash
            stringRedisTemplate.opsForHash().putAll(commentKey, commentMap);
        }
        return noteVo;
    }

    /**
     * 获取我的笔记
     * @param pageQueryDTO
     * @return 我的笔记
     */
    @Override
    public PageResult getMyNotes(PageQueryDTO pageQueryDTO) {
        pageQueryDTO.setUserId(BaseContext.getCurrentId());
        PageHelper.startPage(pageQueryDTO.getPage(),pageQueryDTO.getPageSize());
        Page<Note> page = noteMapper.pageQuery(pageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 评论笔记
     * @param remark
     * @param id
     */
    @Override
    public void comment(String remark,Long id) {
        String key = COMMENT_NOTE + id;
        //先更新数据库
        Comment comment = new Comment();
        comment.setRemark(remark);
        comment.setCommentTime(LocalDateTime.now());
        comment.setUserId(BaseContext.getCurrentId());
        commentMapper.insert(comment);
        //再删除缓存
        stringRedisTemplate.delete(key);
    }

    /**
     * 点赞笔记
     * @param id
     */
    @Override
    public void like(Long id) {
        String key = LIKE_NOTE + id;
        //先更新数据库
        Like like = new Like();
        like.setNoteUserId(id);
        like.setUserId(BaseContext.getCurrentId());
        likeMapper.insert(like);
        //再删除缓存
        stringRedisTemplate.delete(key);
    }

    /**
     * 获取点赞过的笔记
     * @return 笔记
     */
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

    /**
     * 举报违规笔记
     * @param reportDTO
     */
    @Override
    public void report(ReportDTO reportDTO) {
        Report report = new Report();
        BeanUtils.copyProperties(reportDTO,report);
        report.setReportStatus(0);
        report.setReportUserId(BaseContext.getCurrentId());
        report.setCreatTime(LocalDateTime.now());
        reportMapper.insert(report);
    }

    /**
     * 笔记点击量排行榜前10
     * @return 笔记列表
     */
    @Override
    public List<Note> rank() {
        //用redis的zset根据score值获取笔记点击量前10
        Set<String> noteIds = stringRedisTemplate.opsForZSet().reverseRange(CLICK_NOTE, 0, 9);
        List<String> list = noteIds != null ? noteIds.stream().toList() : List.of();
        List<Note> noteList = new ArrayList<>();
        for (String id : list) {
            Long noteId = Long.valueOf(id);
            Note note = noteMapper.selectById(noteId);
            noteList.add(note);
        }
        return noteList;
    }

    /**
     * 动态搜索笔记
     * @param describe
     * @param year
     * @return 笔记列表
     */
    @Override
    public List<Note> conditionSearch(String describe, int year) {
        QueryWrapper<Note> queryWrapper = new QueryWrapper<>();
        // 模糊查询笔记描述
        if (describe != null && !describe.isEmpty()) {
            queryWrapper.like("describe", describe);
        }
        // 按照发布年份倒序查询
        if (year != 0) {
            // 定义该年份的开始和结束时间
            LocalDateTime start = LocalDateTime.of(year, Month.JANUARY, 1, 0, 0);
            LocalDateTime end = LocalDateTime.of(year, Month.DECEMBER, 31, 23, 59, 59);
            // 小于指定日期
            queryWrapper.between("note_time", start,end)
                    .orderByDesc("note_time");
        }
        return noteMapper.selectList(queryWrapper);
    }
}
