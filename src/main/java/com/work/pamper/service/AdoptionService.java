package com.work.pamper.service;

import com.work.pamper.entity.Adoption;
import com.work.pamper.entity.AdoptionApplication;
import org.springframework.stereotype.Service;

@Service
public interface AdoptionService {
    // 创建领养信息
    Object createAdoption(String token, Adoption adoption);

    // 获取领养详情
    Object getAdoptionDetail(Long adoptionId);

    // 分页获取领养列表
    Object getAdoptionList(String petType, Integer page, Integer pageSize);

    // 获取我发布的领养列表
    Object getMyAdoptions(String token, Integer page, Integer pageSize);

    // 更新领养状态
    Object updateAdoptionStatus(String token, Long adoptionId, Integer status);

    // 删除领养信息
    Object deleteAdoption(String token, Long adoptionId);

    // 申请领养
    Object applyAdoption(String token, AdoptionApplication application);

    // 获取领养信息的申请列表（发布者查看）
    Object getApplicationsByAdoption(String token, Long adoptionId);

    // 获取我的申请列表
    Object getMyApplications(String token, Integer page, Integer pageSize);

    // 审核申请（发布者操作）
    Object reviewApplication(String token, Long applicationId, Integer status);
}
