package com.work.pamper.entity;

import lombok.Data;

@Data
public class Post {
    private Long id;
    private Long user_id;
    private String title;
    private String content;
    private String images;        // 多图URL，逗号分隔
    private String video;         // 视频URL
    private Integer post_type;    // 0纯文字，1图文，2视频
    private String category;      // 分类
    private Integer status;       // 0待审核，1已发布，2已拒绝，3已删除
    private Integer like_count;
    private Integer comment_count;
    private Integer view_count;
    private String create_time;
    private String update_time;

    // 非数据库字段：关联用户信息
    private String username;
    private String avatar;

    // 非数据库字段：当前用户是否已点赞
    private Boolean is_liked;
}
