package com.work.pamper.entity;

import lombok.Data;

@Data
public class Notice {
    private Long id;
    private Long notice_type;
    private Long sender;
    private Long accepter;
    private String message;
    // 创建时间
    private String create_time;
}
