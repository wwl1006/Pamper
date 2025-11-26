package com.work.pamper.entity;

import lombok.Data;

@Data
public class Notice {
    private Long id;
    private Integer notice_type;  // 0=私信 1=系统公告
    private Long sender_id;
    private Long receiver_id;
    private String message;
    private Integer is_read;  // 0=未读 1=已读
    private String create_time;

    // 非数据库字段
    private String receiverUsername;
}
