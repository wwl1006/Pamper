package com.work.pamper.entity;

import lombok.Data;

@Data
public class PostComment {
    private Long id;
    private Long post_id;
    private Long user_id;
    private Long parent_id;  // 暂时不用，预留扩展
    private String content;
    private String create_time;

    // 非数据库字段：关联用户信息
    private String username;
    private String avatar;
}
