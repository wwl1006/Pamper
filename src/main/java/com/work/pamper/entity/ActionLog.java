package com.work.pamper.entity;

import lombok.Data;

@Data
public class ActionLog {
    private Long id;
    private Long account_id;
    private String model;
    private String action;
    // 创建时间
    private String create_time;
}
