package com.cs183.tasty.mapper;

import com.cs183.tasty.entity.pojo.Report;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReportMapper extends MPJBaseMapper<Report> {

//    @Insert("insert into report (report_note_id, report_user_id, report_context, report_status, creat_time) VALUES " +
//            "(#{report_note_id},#{report_user_id},#{report_context},#{report_status},#{creat_time})")
//    void addReport(Report report);
}
