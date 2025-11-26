package com.work.pamper.entity;

import lombok.Data;

@Data
public class Activity {
    private Long id;
    private String title;
    private String content;
    private String cover_image;
    private String images;  // 活动图片列表，逗号分隔
    private String activity_type;
    private String location;
    private String start_time;
    private String end_time;
    private String registration_deadline;
    private Integer max_participants;
    private Integer current_participants;
    private String organizer;
    private String contact;
    private Integer status;  // 0=待审核 1=已发布 2=已拒绝 3=已取消 4=进行中 5=已结束
    private Integer view_count;
    private Long create_by;
    private String create_time;
    private String update_time;

    // 非数据库字段
    private String creator_name;  // 创建者用户名
    private Boolean is_registered;  // 当前用户是否已报名
    private Boolean is_full;  // 是否已报满
}
