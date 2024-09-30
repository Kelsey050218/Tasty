package com.cs183.tasty.service.impl;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cs183.tasty.entity.DTO.AdminRegisterDTO;
import com.cs183.tasty.entity.pojo.Admin;
import com.cs183.tasty.entity.pojo.Note;
import com.cs183.tasty.entity.pojo.Report;
import com.cs183.tasty.entity.pojo.User;
import com.cs183.tasty.mapper.*;
import com.cs183.tasty.service.AdminService;
import com.cs183.tasty.utils.RedisConstants;
import com.github.yulichang.query.MPJQueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.cs183.tasty.constant.MessageConstant.*;
import static com.cs183.tasty.utils.RedisConstants.VERIFY_CODE;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private ReportMapper reportMapper;

    @Autowired
    private NoteMapper noteMapper;

    @Override
    public void adminRegister(AdminRegisterDTO adminRegisterDTO) throws Exception{
        String username = adminRegisterDTO.getAdminName();
        MPJQueryWrapper<Admin> wrapper = new MPJQueryWrapper<>();
        wrapper.selectAll(Admin.class)
                .eq("admin_name", username);
        Admin admin = adminMapper.selectOne(wrapper);
        //The user already exists. Please log in directly
        if (admin != null) {
            throw new Exception(ALREADY_EXISTS);
        }
        //Check verification code
        String code = (String) redisTemplate.opsForValue().get(VERIFY_CODE);
        if(StrUtil.isBlank(code)){
            throw new Exception(NUMBER_CODE_EXPIRED);
        }
        if(adminRegisterDTO.getCode().equals(code)){
            redisTemplate.delete(VERIFY_CODE);
            Admin newAdmin = new Admin();
            BeanUtils.copyProperties(adminRegisterDTO,newAdmin);
            newAdmin.setCreateTime(LocalDateTime.now());
            adminMapper.insert(newAdmin);
            menuMapper.bondAdminRole(newAdmin.getAdminId());
        }else{
            redisTemplate.delete(VERIFY_CODE);
            throw new Exception(NUMBER_CODE_NOT_EQUAL);
        }

    }

    @Override
    public void handleReport(Long id,Integer isSuccess) {
        Report report = reportMapper.selectById(id);
        report.setReportStatus(isSuccess);
        report.setHandleTime(LocalDateTime.now());
        reportMapper.updateById(report);
        //如果举报成功
        if(isSuccess == 1){
            Long noteId = report.getReportNoteId();
            Note note = noteMapper.selectById(noteId);
            //就把笔记状态设为异常
            note.setStatus(2);
        }
    }
}
