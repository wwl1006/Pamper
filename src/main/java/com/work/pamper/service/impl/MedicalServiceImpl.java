package com.work.pamper.service.impl;

import com.work.pamper.entity.ServiceInfo;
import com.work.pamper.entity.ServiceApplication;
import com.work.pamper.entity.ServiceAppointment;
import com.work.pamper.mapper.ServiceInfoMapper;
import com.work.pamper.mapper.ServiceApplicationMapper;
import com.work.pamper.mapper.ServiceAppointmentMapper;
import com.work.pamper.service.MedicalService;
import com.work.pamper.utils.JwtUtils;
import com.work.pamper.utils.ResultUtil;
import com.work.pamper.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MedicalServiceImpl implements MedicalService {
    @Autowired
    ServiceInfoMapper serviceInfoMapper;

    @Autowired
    ServiceApplicationMapper serviceApplicationMapper;

    @Autowired
    ServiceAppointmentMapper serviceAppointmentMapper;

    private static final String UNAUTHORIZED_MSG = "登录状态已失效，请重新登录";

    private Long parseUserIdFromToken(String token) {
        if (token == null) return null;
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        try {
            return Long.valueOf(JwtUtils.getSubject(token));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    @Transactional
    public Object createService(String token, ServiceInfo serviceInfo) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        if (serviceInfo.getTitle() == null || serviceInfo.getTitle().trim().isEmpty()) {
            return ResultUtil.error("标题不能为空");
        }

        if (serviceInfo.getService_type() == null || serviceInfo.getService_type().trim().isEmpty()) {
            return ResultUtil.error("服务类型不能为空");
        }

        serviceInfo.setUser_id(userId);
        serviceInfo.setCreate_time(TimeUtils.getCurrentTimeString());
        serviceInfo.setUpdate_time(TimeUtils.getCurrentTimeString());
        serviceInfo.setStatus(1);  // 先发布再审核
        serviceInfo.setView_count(0);
        serviceInfo.setApplication_count(0);

        try {
            int result = serviceInfoMapper.createServiceInfo(serviceInfo);
            if (result > 0) {
                return ResultUtil.success("发布成功", serviceInfo);
            } else {
                return ResultUtil.error("发布失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("发布失败：" + e.getMessage());
        }
    }

    @Override
    public Object getServiceDetail(String token, Long serviceId) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            ServiceInfo service = serviceInfoMapper.getServiceInfoById(serviceId);
            if (service == null) {
                return ResultUtil.error("服务不存在");
            }

            // 增加浏览次数
            serviceInfoMapper.increaseViewCount(serviceId);

            return ResultUtil.success("获取成功", service);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败：" + e.getMessage());
        }
    }

    @Override
    public Object getServiceList(String serviceType, Integer publishType, Integer page, Integer pageSize) {
        try {
            if (page == null || page < 1) page = 1;
            if (pageSize == null || pageSize < 1) pageSize = 10;

            int offset = (page - 1) * pageSize;
            List<ServiceInfo> list = serviceInfoMapper.getServiceList(serviceType, publishType, 1, offset, pageSize);
            int total = serviceInfoMapper.getServiceCount(serviceType, publishType, 1);

            Map<String, Object> data = new HashMap<>();
            data.put("list", list);
            data.put("total", total);
            data.put("page", page);
            data.put("pageSize", pageSize);

            return ResultUtil.success("获取成功", data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败：" + e.getMessage());
        }
    }

    @Override
    public Object getMyServices(String token) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            List<ServiceInfo> services = serviceInfoMapper.getServicesByUserId(userId);
            return ResultUtil.success("获取成功", services);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Object updateService(String token, ServiceInfo serviceInfo) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            ServiceInfo existing = serviceInfoMapper.getServiceInfoById(serviceInfo.getId());
            if (existing == null) {
                return ResultUtil.error("服务不存在");
            }

            if (!userId.equals(existing.getUser_id())) {
                return ResultUtil.error(403, "无权修改此服务");
            }

            serviceInfo.setUpdate_time(TimeUtils.getCurrentTimeString());
            int result = serviceInfoMapper.updateServiceInfo(serviceInfo);
            if (result > 0) {
                return ResultUtil.success("更新成功");
            } else {
                return ResultUtil.error("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("更新失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Object deleteService(String token, Long serviceId) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            ServiceInfo service = serviceInfoMapper.getServiceInfoById(serviceId);
            if (service == null) {
                return ResultUtil.error("服务不存在");
            }

            if (!userId.equals(service.getUser_id())) {
                return ResultUtil.error(403, "无权删除此服务");
            }

            int result = serviceInfoMapper.deleteServiceInfo(serviceId);
            if (result > 0) {
                return ResultUtil.success("删除成功");
            } else {
                return ResultUtil.error("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("删除失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Object applyForService(String token, ServiceApplication application) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            ServiceInfo service = serviceInfoMapper.getServiceInfoById(application.getService_id());
            if (service == null) {
                return ResultUtil.error("服务不存在");
            }

            // 检查是否是自己的服务
            if (userId.equals(service.getUser_id())) {
                return ResultUtil.error("不能申请自己发布的服务");
            }

            // 检查是否已申请
            int count = serviceApplicationMapper.checkUserApplied(application.getService_id(), userId);
            if (count > 0) {
                return ResultUtil.error("您已申请过此服务");
            }

            application.setUser_id(userId);
            application.setStatus(0);  // 待审核
            application.setCreate_time(TimeUtils.getCurrentTimeString());
            application.setUpdate_time(TimeUtils.getCurrentTimeString());

            int result = serviceApplicationMapper.createApplication(application);
            if (result > 0) {
                // 增加申请数量
                serviceInfoMapper.increaseApplicationCount(application.getService_id());
                return ResultUtil.success("申请成功", application);
            } else {
                return ResultUtil.error("申请失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("申请失败：" + e.getMessage());
        }
    }

    @Override
    public Object getServiceApplications(String token, Long serviceId) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            ServiceInfo service = serviceInfoMapper.getServiceInfoById(serviceId);
            if (service == null) {
                return ResultUtil.error("服务不存在");
            }

            // 只有服务发布者才能查看申请列表
            if (!userId.equals(service.getUser_id())) {
                return ResultUtil.error(403, "无权查看申请列表");
            }

            List<ServiceApplication> applications = serviceApplicationMapper.getApplicationsByServiceId(serviceId);
            return ResultUtil.success("获取成功", applications);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败：" + e.getMessage());
        }
    }

    @Override
    public Object getMyApplications(String token) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            List<ServiceApplication> applications = serviceApplicationMapper.getApplicationsByUserId(userId);
            return ResultUtil.success("获取成功", applications);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Object reviewApplication(String token, Long applicationId, Integer status, String reply) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            ServiceApplication application = serviceApplicationMapper.getApplicationById(applicationId);
            if (application == null) {
                return ResultUtil.error("申请不存在");
            }

            ServiceInfo service = serviceInfoMapper.getServiceInfoById(application.getService_id());
            if (!userId.equals(service.getUser_id())) {
                return ResultUtil.error(403, "无权审核此申请");
            }

            int result = serviceApplicationMapper.updateApplicationStatus(applicationId, status, reply);
            if (result > 0) {
                return ResultUtil.success("审核成功");
            } else {
                return ResultUtil.error("审核失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("审核失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Object cancelApplication(String token, Long applicationId) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            ServiceApplication application = serviceApplicationMapper.getApplicationById(applicationId);
            if (application == null) {
                return ResultUtil.error("申请不存在");
            }

            if (!userId.equals(application.getUser_id())) {
                return ResultUtil.error(403, "无权取消此申请");
            }

            int result = serviceApplicationMapper.updateApplicationStatus(applicationId, 3, null);
            if (result > 0) {
                return ResultUtil.success("取消成功");
            } else {
                return ResultUtil.error("取消失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("取消失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Object createAppointment(String token, ServiceAppointment appointment) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            // 检查申请是否存在且已通过
            ServiceApplication application = serviceApplicationMapper.getApplicationById(appointment.getApplication_id());
            if (application == null) {
                return ResultUtil.error("申请不存在");
            }

            if (application.getStatus() != 1) {
                return ResultUtil.error("申请未通过，无法创建预约");
            }

            // 检查是否已存在预约
            ServiceAppointment existing = serviceAppointmentMapper.getAppointmentByApplicationId(appointment.getApplication_id());
            if (existing != null) {
                return ResultUtil.error("已存在预约记录");
            }

            appointment.setService_id(application.getService_id());
            appointment.setUser_id(userId);
            appointment.setStatus(0);  // 待确认
            appointment.setCreate_time(TimeUtils.getCurrentTimeString());
            appointment.setUpdate_time(TimeUtils.getCurrentTimeString());

            int result = serviceAppointmentMapper.createAppointment(appointment);
            if (result > 0) {
                return ResultUtil.success("预约成功", appointment);
            } else {
                return ResultUtil.error("预约失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("预约失败：" + e.getMessage());
        }
    }

    @Override
    public Object getMyAppointments(String token) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            List<ServiceAppointment> appointments = serviceAppointmentMapper.getAppointmentsByUserId(userId);
            return ResultUtil.success("获取成功", appointments);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Object updateAppointment(String token, ServiceAppointment appointment) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            ServiceAppointment existing = serviceAppointmentMapper.getAppointmentById(appointment.getId());
            if (existing == null) {
                return ResultUtil.error("预约不存在");
            }

            if (!userId.equals(existing.getUser_id())) {
                return ResultUtil.error(403, "无权修改此预约");
            }

            appointment.setUpdate_time(TimeUtils.getCurrentTimeString());
            int result = serviceAppointmentMapper.updateAppointment(appointment);
            if (result > 0) {
                return ResultUtil.success("更新成功");
            } else {
                return ResultUtil.error("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("更新失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Object updateAppointmentStatus(String token, Long appointmentId, Integer status) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            ServiceAppointment appointment = serviceAppointmentMapper.getAppointmentById(appointmentId);
            if (appointment == null) {
                return ResultUtil.error("预约不存在");
            }

            if (!userId.equals(appointment.getUser_id())) {
                return ResultUtil.error(403, "无权修改此预约");
            }

            int result = serviceAppointmentMapper.updateAppointmentStatus(appointmentId, status);
            if (result > 0) {
                return ResultUtil.success("更新成功");
            } else {
                return ResultUtil.error("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("更新失败：" + e.getMessage());
        }
    }
}
