package com.work.pamper.entity;

import lombok.Data;

@Data
public class ActivityRegistration {
    private Long id;
    private Long activity_id;
    private Long user_id;
    private String real_name;
    private String phone;
    private String email;
    private String message;
    private Integer status;  // 0=已取消 1=已报名 2=已签到
    private String create_time;
    private String update_time;

    // 非数据库字段
    private String username;  // 用户名
    private String avatar;  // 用户头像
    private String activity_title;  // 活动标题
    private String start_time;  // 活动开始时间
}
