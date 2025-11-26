package com.work.pamper.mapper;

import com.work.pamper.entity.PetProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PetProfileMapper {
    // 创建宠物档案
    int createPetProfile(PetProfile petProfile);

    // 根据ID获取宠物档案
    PetProfile getPetProfileById(@Param("id") Long id);

    // 获取用户的宠物列表
    List<PetProfile> getPetsByUserId(@Param("userId") Long userId);

    // 更新宠物档案
    int updatePetProfile(PetProfile petProfile);

    // 删除宠物档案
    int deletePetProfile(@Param("id") Long id);

    // 管理员功能
    int getTotalPetCount();
}
