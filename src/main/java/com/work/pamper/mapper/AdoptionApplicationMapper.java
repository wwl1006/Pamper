package com.work.pamper.mapper;

import com.work.pamper.entity.AdoptionApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdoptionApplicationMapper {
    // 创建领养申请
    int createApplication(AdoptionApplication application);

    // 根据ID获取申请
    AdoptionApplication getApplicationById(@Param("id") Long id);

    // 获取领养信息的申请列表（带申请人信息）
    List<AdoptionApplication> getApplicationsByAdoptionId(@Param("adoptionId") Long adoptionId);

    // 获取用户提交的申请列表（带领养信息）
    List<AdoptionApplication> getApplicationsByApplicantId(
        @Param("applicantId") Long applicantId,
        @Param("offset") int offset,
        @Param("limit") int limit
    );

    // 统计领养信息的申请数量
    long countApplicationsByAdoptionId(@Param("adoptionId") Long adoptionId);

    // 检查用户是否已申请
    int checkApplication(@Param("adoptionId") Long adoptionId, @Param("applicantId") Long applicantId);

    // 更新申请状态
    int updateApplicationStatus(@Param("id") Long id, @Param("status") Integer status);

    // 删除申请
    int deleteApplication(@Param("id") Long id);
}
