package com.work.pamper.entity;

import lombok.Data;
import org.springframework.context.annotation.Bean;

@Data
public class Profile {
    private String username;
    private String email;
    private String description;
}
