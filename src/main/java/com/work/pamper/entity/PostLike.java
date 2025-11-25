package com.work.pamper.entity;

import lombok.Data;

@Data
public class PostLike {
    private Long id;
    private Long post_id;
    private Long user_id;
    private String create_time;
}
