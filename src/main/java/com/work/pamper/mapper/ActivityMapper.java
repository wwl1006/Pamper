package com.work.pamper.mapper;

import com.work.pamper.entity.Activity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ActivityMapper {
    // 创建活动
    int createActivity(Activity activity);

    // 根据ID获取活动
    Activity getActivityById(@Param("id") Long id, @Param("userId") Long userId);

    // 获取活动列表（分页）
    List<Activity> getActivityList(@Param("status") Integer status,
                                   @Param("activityType") String activityType,
                                   @Param("offset") int offset,
                                   @Param("limit") int limit,
                                   @Param("userId") Long userId);

    // 获取活动总数
    int getActivityCount(@Param("status") Integer status,
                        @Param("activityType") String activityType);

    // 获取管理员创建的活动列表
    List<Activity> getActivitiesByCreator(@Param("createBy") Long createBy);

    // 更新活动信息
    int updateActivity(Activity activity);

    // 删除活动
    int deleteActivity(@Param("id") Long id);

    // 更新活动状态
    int updateActivityStatus(@Param("id") Long id, @Param("status") Integer status);

    // 增加浏览次数
    int increaseViewCount(@Param("id") Long id);

    // 增加报名人数
    int increaseParticipants(@Param("id") Long id);

    // 减少报名人数
    int decreaseParticipants(@Param("id") Long id);

    // 管理员功能
    @MapKey("id")
    List<Map<String, Object>> getActivitiesByStatus(@Param("status") int status,
                                                    @Param("offset") int offset,
                                                    @Param("limit") int limit);
    @MapKey("type")
    List<Map<String, Object>> getActivityCountByType();
    int getTotalRegistrationCount();
    @MapKey("id")
    List<Map<String, Object>> getAllActivitiesForAdmin(@Param("offset") int offset,
                                                        @Param("limit") int limit);
    int countAllActivities();
}
