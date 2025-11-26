package com.work.pamper.mapper;

import com.work.pamper.entity.ServiceApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ServiceApplicationMapper {
    // 创建服务申请
    int createApplication(ServiceApplication application);

    // 根据ID获取申请
    ServiceApplication getApplicationById(@Param("id") Long id);

    // 获取服务的申请列表
    List<ServiceApplication> getApplicationsByServiceId(@Param("serviceId") Long serviceId);

    // 获取用户的申请列表
    List<ServiceApplication> getApplicationsByUserId(@Param("userId") Long userId);

    // 检查用户是否已申请
    int checkUserApplied(@Param("serviceId") Long serviceId, @Param("userId") Long userId);

    // 更新申请状态
    int updateApplicationStatus(@Param("id") Long id, @Param("status") Integer status, @Param("reply") String reply);

    // 删除申请
    int deleteApplication(@Param("id") Long id);
}
