package com.work.pamper.entity;

import lombok.Data;

@Data
public class Follow {
    private Long id;
    private Long followers;
    private Long followed;
    // 创建时间
    private String create_time;
}
