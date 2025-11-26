package com.work.pamper.entity;

import lombok.Data;

@Data
public class News {
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String cover_image;
    private String category;
    private String tags;
    private String source;
    private String author;
    private Integer status;  // 0=草稿 1=已发布 2=已下架
    private Integer is_top;  // 0=否 1=是
    private Integer view_count;
    private Integer like_count;
    private Long create_by;
    private String create_time;
    private String update_time;
    private String publish_time;

    // 非数据库字段
    private String creator_name;  // 创建者用户名
}
