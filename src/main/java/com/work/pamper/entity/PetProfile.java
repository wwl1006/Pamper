package com.work.pamper.entity;

import lombok.Data;

@Data
public class PetProfile {
    private Long id;
    private Long user_id;
    private String pet_name;
    private String pet_type;       // 猫、狗、其他
    private String breed;          // 品种
    private String gender;         // 公、母
    private String birthday;       // 出生日期
    private String color;          // 毛色
    private Double weight;         // 体重(kg)
    private String description;    // 备注说明
    private String avatar;         // 宠物头像
    private String create_time;
    private String update_time;

    // 非数据库字段：健康记录数量
    private Integer health_record_count;
}
