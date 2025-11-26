package com.work.pamper.entity;

import lombok.Data;

@Data
public class HealthRecord {
    private Long id;
    private Long pet_id;
    private String record_type;    // 疫苗、驱虫、体检、诊疗、其他
    private String title;          // 记录标题
    private String content;        // 详细内容
    private String record_date;    // 记录日期
    private String doctor;         // 医生姓名
    private String hospital;       // 医院名称
    private String create_time;

    // 非数据库字段：宠物信息
    private String pet_name;
}
