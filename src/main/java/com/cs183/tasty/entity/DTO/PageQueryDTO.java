package com.cs183.tasty.entity.DTO;

import lombok.Data;

@Data
public class PageQueryDTO {

    private Long userId;

    private int page;

    private int pageSize;

}
