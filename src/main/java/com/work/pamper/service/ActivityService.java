package com.work.pamper.service;

import com.work.pamper.entity.Activity;
import com.work.pamper.entity.ActivityRegistration;
import com.work.pamper.entity.News;
import org.springframework.stereotype.Service;

@Service
public interface ActivityService {
    // 活动管理
    Object createActivity(String token, Activity activity);
    Object getActivityDetail(String token, Long activityId);
    Object getActivityList(String activityType, Integer page, Integer pageSize, String token);
    Object getMyActivities(String token);
    Object updateActivity(String token, Activity activity);
    Object deleteActivity(String token, Long activityId);
    Object updateActivityStatus(String token, Long activityId, Integer status);
    Object uploadActivityCover(String token, org.springframework.web.multipart.MultipartFile file);

    // 活动报名
    Object registerActivity(String token, ActivityRegistration registration);
    Object getActivityRegistrations(String token, Long activityId);
    Object getMyRegistrations(String token);
    Object cancelRegistration(String token, Long registrationId);

    // 资讯管理
    Object createNews(String token, News news);
    Object getNewsDetail(Long newsId);
    Object getNewsList(String category, Integer page, Integer pageSize);
    Object updateNews(String token, News news);
    Object deleteNews(String token, Long newsId);
    Object updateNewsStatus(String token, Long newsId, Integer status);
    Object updateNewsTop(String token, Long newsId, Integer isTop);
    Object likeNews(Long newsId);
}
