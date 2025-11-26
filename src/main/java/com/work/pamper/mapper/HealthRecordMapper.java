package com.work.pamper.mapper;

import com.work.pamper.entity.HealthRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HealthRecordMapper {
    // 添加健康记录
    int addHealthRecord(HealthRecord record);

    // 根据ID获取健康记录
    HealthRecord getHealthRecordById(@Param("id") Long id);

    // 获取宠物的健康记录列表
    List<HealthRecord> getRecordsByPetId(@Param("petId") Long petId);

    // 统计宠物的健康记录数量
    long countRecordsByPetId(@Param("petId") Long petId);

    // 更新健康记录
    int updateHealthRecord(HealthRecord record);

    // 删除健康记录
    int deleteHealthRecord(@Param("id") Long id);
}
