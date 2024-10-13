package com.cs183.tasty.entity.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Follow {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;                 // 主键，自动递增

    private Long userId;            // 用户ID

    private Long followUserId;      // 被关注用户ID

    private LocalDateTime createTime; // 创建时间

}
