package com.work.pamper.entity;

import lombok.Data;

@Data
public class Account {
    private Long id;
    private String username;
    private String password;
    private String  avatar;
    private Long user_type;
    private String email;
    private String description;
    private String create_time;
}
