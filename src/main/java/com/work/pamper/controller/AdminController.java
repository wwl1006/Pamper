package com.work.pamper.controller;

import com.work.pamper.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    AdminService adminService;

    // 用户管理
    @GetMapping("/users")
    public Object getUserList(@RequestParam(required = false) Integer page,
                             @RequestParam(required = false) Integer pageSize) {
        return adminService.getUserList(page, pageSize);
    }

    @PutMapping("/users/{userId}/status")
    public Object updateUserStatus(@RequestHeader(value = "Authorization", required = false) String token,
                                   @PathVariable Long userId,
                                   @RequestParam Integer status) {
        return adminService.updateUserStatus(token, userId, status);
    }

    // 帖子审核
    @GetMapping("/posts/pending")
    public Object getPendingPosts(@RequestHeader(value = "Authorization", required = false) String token,
                                  @RequestParam(required = false) Integer page,
                                  @RequestParam(required = false) Integer pageSize) {
        return adminService.getPendingPosts(token, page, pageSize);
    }

    @GetMapping("/posts/all")
    public Object getAllPosts(@RequestHeader(value = "Authorization", required = false) String token,
                             @RequestParam(required = false) Integer page,
                             @RequestParam(required = false) Integer pageSize) {
        return adminService.getAllPosts(token, page, pageSize);
    }

    @PutMapping("/posts/{postId}/review")
    public Object reviewPost(@RequestHeader(value = "Authorization", required = false) String token,
                            @PathVariable Long postId,
                            @RequestParam Integer status) {
        return adminService.reviewPost(token, postId, status);
    }

    // 领养审核
    @GetMapping("/adoptions/pending")
    public Object getPendingAdoptions(@RequestHeader(value = "Authorization", required = false) String token,
                                      @RequestParam(required = false) Integer page,
                                      @RequestParam(required = false) Integer pageSize) {
        return adminService.getPendingAdoptions(token, page, pageSize);
    }

    @GetMapping("/adoptions/all")
    public Object getAllAdoptions(@RequestHeader(value = "Authorization", required = false) String token,
                                  @RequestParam(required = false) Integer page,
                                  @RequestParam(required = false) Integer pageSize) {
        return adminService.getAllAdoptions(token, page, pageSize);
    }

    @PutMapping("/adoptions/{adoptionId}/review")
    public Object reviewAdoption(@RequestHeader(value = "Authorization", required = false) String token,
                                 @PathVariable Long adoptionId,
                                 @RequestParam Integer status) {
        return adminService.reviewAdoption(token, adoptionId, status);
    }

    // 活动审核
    @GetMapping("/activities/pending")
    public Object getPendingActivities(@RequestHeader(value = "Authorization", required = false) String token,
                                       @RequestParam(required = false) Integer page,
                                       @RequestParam(required = false) Integer pageSize) {
        return adminService.getPendingActivities(token, page, pageSize);
    }

    @GetMapping("/activities/all")
    public Object getAllActivities(@RequestHeader(value = "Authorization", required = false) String token,
                                   @RequestParam(required = false) Integer page,
                                   @RequestParam(required = false) Integer pageSize) {
        return adminService.getAllActivities(token, page, pageSize);
    }

    @PutMapping("/activities/{activityId}/review")
    public Object reviewActivity(@RequestHeader(value = "Authorization", required = false) String token,
                                 @PathVariable Long activityId,
                                 @RequestParam Integer status) {
        return adminService.reviewActivity(token, activityId, status);
    }

    // 平台统计（保留但简化）
    @GetMapping("/dashboard/stats")
    public Object getDashboardStats(@RequestHeader(value = "Authorization", required = false) String token) {
        return adminService.getDashboardStats(token);
    }

    @GetMapping("/dashboard/user-growth")
    public Object getUserGrowthData(@RequestHeader(value = "Authorization", required = false) String token,
                                    @RequestParam(required = false) Integer days) {
        return adminService.getUserGrowthData(token, days);
    }

    @GetMapping("/dashboard/content-stats")
    public Object getContentStats(@RequestHeader(value = "Authorization", required = false) String token) {
        return adminService.getContentStats(token);
    }

    @GetMapping("/dashboard/activity-stats")
    public Object getActivityStats(@RequestHeader(value = "Authorization", required = false) String token) {
        return adminService.getActivityStats(token);
    }
}
