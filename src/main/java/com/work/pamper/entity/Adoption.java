package com.work.pamper.entity;

import lombok.Data;

@Data
public class Adoption {
    private Long id;
    private Long user_id;
    private String pet_name;
    private String pet_type;       // 猫、狗、其他
    private String pet_breed;      // 品种
    private String pet_age;        // 年龄
    private String pet_gender;     // 公、母、未知
    private String description;    // 描述信息
    private String images;         // 图片URL列表，逗号分隔
    private String contact;        // 联系方式
    private String location;       // 所在地区
    private Integer status;        // 0待审核，1已发布，2已拒绝，3已领养，4已下架
    private String create_time;
    private String update_time;

    // 非数据库字段：关联用户信息
    private String username;
    private String avatar;

    // 非数据库字段：申请数量
    private Integer application_count;
}
