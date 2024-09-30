package com.cs183.tasty.entity.pojo;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName(value ="report")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report implements Serializable {

    @TableId(value = "report_id",type = IdType.AUTO)
    private Long reportId;

    private Long reportNoteId;//被举报笔记id

    private Long reportUserId;//举报者id

    private String reportContext;

    private Integer reportStatus;//举报情况：0为待审核，1为举报成功，2为举报失败

    private LocalDateTime creatTime;

    private LocalDateTime handleTime;


}
