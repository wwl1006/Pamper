package com.work.pamper.entity;

import lombok.Data;

@Data
public class ServiceApplication {
    private Long id;
    private Long service_id;
    private Long user_id;
    private Long pet_id;
    private String message;
    private String contact;
    private Integer status;  // 0=待审核 1=已通过 2=已拒绝 3=已取消
    private String reply;
    private String create_time;
    private String update_time;

    // 非数据库字段
    private String username;     // 申请者用户名
    private String avatar;       // 申请者头像
    private String pet_name;     // 宠物名称
    private String service_title; // 服务标题
}
