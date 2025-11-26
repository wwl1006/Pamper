package com.work.pamper.controller;

import com.work.pamper.entity.ServiceInfo;
import com.work.pamper.entity.ServiceApplication;
import com.work.pamper.entity.ServiceAppointment;
import com.work.pamper.service.MedicalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/service")
public class MedicalController {

    @Autowired
    private MedicalService medicalService;

    // 服务信息管理
    @PostMapping("/create")
    public Object createService(@RequestHeader("Authorization") String token,
                               @RequestBody ServiceInfo serviceInfo) {
        return medicalService.createService(token, serviceInfo);
    }

    @GetMapping("/{id:\\d+}")
    public Object getServiceDetail(@RequestHeader("Authorization") String token,
                                   @PathVariable Long id) {
        return medicalService.getServiceDetail(token, id);
    }

    @GetMapping("/list")
    public Object getServiceList(@RequestParam(required = false) String serviceType,
                                @RequestParam(required = false) Integer publishType,
                                @RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer pageSize) {
        return medicalService.getServiceList(serviceType, publishType, page, pageSize);
    }

    @GetMapping("/my")
    public Object getMyServices(@RequestHeader("Authorization") String token) {
        return medicalService.getMyServices(token);
    }

    @PutMapping("/update")
    public Object updateService(@RequestHeader("Authorization") String token,
                               @RequestBody ServiceInfo serviceInfo) {
        return medicalService.updateService(token, serviceInfo);
    }

        @DeleteMapping("/{id:\\d+}")
    public Object deleteService(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        return medicalService.deleteService(token, id);
    }

    // 服务申请管理
    @PostMapping("/apply")
    public Object applyForService(@RequestHeader("Authorization") String token,
                                 @RequestBody ServiceApplication application) {
        return medicalService.applyForService(token, application);
    }

    @GetMapping("/applications/{serviceId:\\d+}")
    public Object getServiceApplications(@RequestHeader("Authorization") String token,
                                        @PathVariable Long serviceId) {
        return medicalService.getServiceApplications(token, serviceId);
    }

    @GetMapping("/applications/my")
    public Object getMyApplications(@RequestHeader("Authorization") String token) {
        return medicalService.getMyApplications(token);
    }

    @PutMapping("/application/review")
    public Object reviewApplication(@RequestHeader("Authorization") String token,
                                   @RequestParam Long applicationId,
                                   @RequestParam Integer status,
                                   @RequestParam(required = false) String reply) {
        return medicalService.reviewApplication(token, applicationId, status, reply);
    }

    @PutMapping("/application/cancel/{id:\\d+}")
    public Object cancelApplication(@RequestHeader("Authorization") String token,
                                   @PathVariable Long id) {
        return medicalService.cancelApplication(token, id);
    }

    // 预约管理
    @PostMapping("/appointment/create")
    public Object createAppointment(@RequestHeader("Authorization") String token,
                                   @RequestBody ServiceAppointment appointment) {
        return medicalService.createAppointment(token, appointment);
    }

    @GetMapping("/appointments/my")
    public Object getMyAppointments(@RequestHeader("Authorization") String token) {
        return medicalService.getMyAppointments(token);
    }

    @PutMapping("/appointment/update")
    public Object updateAppointment(@RequestHeader("Authorization") String token,
                                   @RequestBody ServiceAppointment appointment) {
        return medicalService.updateAppointment(token, appointment);
    }

    @PutMapping("/appointment/status")
    public Object updateAppointmentStatus(@RequestHeader("Authorization") String token,
                                         @RequestParam Long appointmentId,
                                         @RequestParam Integer status) {
        return medicalService.updateAppointmentStatus(token, appointmentId, status);
    }
}
