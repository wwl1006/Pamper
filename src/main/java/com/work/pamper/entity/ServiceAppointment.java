package com.work.pamper.entity;

import lombok.Data;

@Data
public class ServiceAppointment {
    private Long id;
    private Long application_id;
    private Long service_id;
    private Long user_id;
    private String appointment_time;
    private String location;
    private String notes;
    private Integer status;  // 0=待确认 1=已确认 2=已完成 3=已取消
    private String create_time;
    private String update_time;

    // 非数据库字段
    private String username;       // 用户名
    private String service_title;  // 服务标题
    private String service_type;   // 服务类型
}
