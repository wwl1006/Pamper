package com.work.pamper.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private Long id;
    private String username;
    private String  avatar;
    private Long user_type;
    private String email;
    private String description;
    private String create_time;
    private String token;
}
