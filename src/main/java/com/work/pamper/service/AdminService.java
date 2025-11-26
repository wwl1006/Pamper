package com.work.pamper.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface AdminService {
    // 用户管理
    Object getUserList(Integer page, Integer pageSize);
    Object updateUserStatus(String token, Long userId, Integer status);

    // 内容审核
    Object getPendingPosts(String token, Integer page, Integer pageSize);
    Object getAllPosts(String token, Integer page, Integer pageSize);
    Object reviewPost(String token, Long postId, Integer status);
    Object getPendingAdoptions(String token, Integer page, Integer pageSize);
    Object getAllAdoptions(String token, Integer page, Integer pageSize);
    Object reviewAdoption(String token, Long adoptionId, Integer status);
    Object getPendingActivities(String token, Integer page, Integer pageSize);
    Object getAllActivities(String token, Integer page, Integer pageSize);
    Object reviewActivity(String token, Long activityId, Integer status);

    // 平台统计
    Object getDashboardStats(String token);
    Object getUserGrowthData(String token, Integer days);
    Object getContentStats(String token);
    Object getActivityStats(String token);
}
