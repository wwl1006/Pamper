package com.work.pamper.service;

import com.work.pamper.entity.ServiceInfo;
import com.work.pamper.entity.ServiceApplication;
import com.work.pamper.entity.ServiceAppointment;
import org.springframework.stereotype.Service;

@Service
public interface MedicalService {
    // 服务信息管理
    Object createService(String token, ServiceInfo serviceInfo);
    Object getServiceDetail(String token, Long serviceId);
    Object getServiceList(String serviceType, Integer publishType, Integer page, Integer pageSize);
    Object getMyServices(String token);
    Object updateService(String token, ServiceInfo serviceInfo);
    Object deleteService(String token, Long serviceId);

    // 服务申请管理
    Object applyForService(String token, ServiceApplication application);
    Object getServiceApplications(String token, Long serviceId);
    Object getMyApplications(String token);
    Object reviewApplication(String token, Long applicationId, Integer status, String reply);
    Object cancelApplication(String token, Long applicationId);

    // 预约管理
    Object createAppointment(String token, ServiceAppointment appointment);
    Object getMyAppointments(String token);
    Object updateAppointment(String token, ServiceAppointment appointment);
    Object updateAppointmentStatus(String token, Long appointmentId, Integer status);
}
