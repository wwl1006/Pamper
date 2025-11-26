package com.work.pamper.entity;

import lombok.Data;

@Data
public class AdoptionApplication {
    private Long id;
    private Long adoption_id;
    private Long applicant_id;
    private String message;        // 申请留言
    private String contact;        // 申请人联系方式
    private Integer status;        // 0待审核，1已通过，2已拒绝
    private String create_time;
    private String update_time;

    // 非数据库字段：申请人信息
    private String applicant_username;
    private String applicant_avatar;

    // 非数据库字段：领养信息
    private String pet_name;
    private String pet_type;
}
