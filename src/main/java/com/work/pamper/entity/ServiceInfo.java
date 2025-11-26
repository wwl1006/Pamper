package com.work.pamper.entity;

import lombok.Data;

@Data
public class ServiceInfo {
    private Long id;
    private Long user_id;
    private String service_type;
    private Integer publish_type;  // 0=用户需求 1=服务提供
    private String title;
    private String content;
    private String images;  // 图片URL列表，逗号分隔
    private String pet_type;
    private String location;
    private String contact;
    private Double price;
    private Integer status;  // 0=待审核 1=已发布 2=已拒绝 3=已下架 4=已完成
    private Integer view_count;
    private Integer application_count;
    private String create_time;
    private String update_time;

    // 非数据库字段
    private String username;  // 发布者用户名
    private String avatar;    // 发布者头像
}
