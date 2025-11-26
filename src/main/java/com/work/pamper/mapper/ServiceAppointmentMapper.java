package com.work.pamper.mapper;

import com.work.pamper.entity.ServiceAppointment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ServiceAppointmentMapper {
    // 创建预约
    int createAppointment(ServiceAppointment appointment);

    // 根据ID获取预约
    ServiceAppointment getAppointmentById(@Param("id") Long id);

    // 根据申请ID获取预约
    ServiceAppointment getAppointmentByApplicationId(@Param("applicationId") Long applicationId);

    // 获取用户的预约列表
    List<ServiceAppointment> getAppointmentsByUserId(@Param("userId") Long userId);

    // 获取服务的预约列表
    List<ServiceAppointment> getAppointmentsByServiceId(@Param("serviceId") Long serviceId);

    // 更新预约信息
    int updateAppointment(ServiceAppointment appointment);

    // 更新预约状态
    int updateAppointmentStatus(@Param("id") Long id, @Param("status") Integer status);

    // 删除预约
    int deleteAppointment(@Param("id") Long id);
}
