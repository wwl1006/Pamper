package com.work.pamper.mapper;

import com.work.pamper.entity.ServiceInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ServiceInfoMapper {
    // 创建服务信息
    int createServiceInfo(ServiceInfo serviceInfo);

    // 根据ID获取服务信息
    ServiceInfo getServiceInfoById(@Param("id") Long id);

    // 获取服务列表（分页）
    List<ServiceInfo> getServiceList(@Param("serviceType") String serviceType,
                                     @Param("publishType") Integer publishType,
                                     @Param("status") Integer status,
                                     @Param("offset") int offset,
                                     @Param("limit") int limit);

    // 获取服务总数
    int getServiceCount(@Param("serviceType") String serviceType,
                       @Param("publishType") Integer publishType,
                       @Param("status") Integer status);

    // 获取用户发布的服务列表
    List<ServiceInfo> getServicesByUserId(@Param("userId") Long userId);

    // 更新服务信息
    int updateServiceInfo(ServiceInfo serviceInfo);

    // 删除服务信息
    int deleteServiceInfo(@Param("id") Long id);

    // 增加浏览次数
    int increaseViewCount(@Param("id") Long id);

    // 增加申请数量
    int increaseApplicationCount(@Param("id") Long id);

    // 更新服务状态
    int updateServiceStatus(@Param("id") Long id, @Param("status") Integer status);
}
