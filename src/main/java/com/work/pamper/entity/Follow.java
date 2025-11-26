package com.work.pamper.entity;

import lombok.Data;

@Data
public class Follow {
    private Long id;
    private Long follower_id;  // 关注者ID
    private Long followed_id;  // 被关注者ID
    private String create_time;
}
