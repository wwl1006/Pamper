package com.work.pamper.entity;

import lombok.Data;

@Data
public class Account {
    private Long id;
    private String username;
    private String password;
    private String avatar;
    private Integer user_type;  // 0=普通用户 1=管理员
    private String email;
    private String description;
    private Integer status;  // 0=禁用 1=正常
    private String create_time;
}
