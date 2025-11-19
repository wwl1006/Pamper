package com.work.pamper.request;

import lombok.Data;

@Data
public class PasswordChangeRequest {
    private String oldPassword;
    private String newPassword;
}
