package com.work.pamper.service;

import com.work.pamper.entity.PetProfile;
import com.work.pamper.entity.HealthRecord;
import org.springframework.stereotype.Service;

@Service
public interface PetService {
    // 创建宠物档案
    Object createPetProfile(String token, PetProfile petProfile);

    // 获取宠物档案详情
    Object getPetProfile(String token, Long petId);

    // 获取我的宠物列表
    Object getMyPets(String token);

    // 更新宠物档案
    Object updatePetProfile(String token, PetProfile petProfile);

    // 删除宠物档案
    Object deletePetProfile(String token, Long petId);

    // 添加健康记录
    Object addHealthRecord(String token, HealthRecord record);

    // 获取宠物的健康记录列表
    Object getHealthRecords(String token, Long petId);

    // 更新健康记录
    Object updateHealthRecord(String token, HealthRecord record);

    // 删除健康记录
    Object deleteHealthRecord(String token, Long recordId);
}
