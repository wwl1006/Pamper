package com.work.pamper.mapper;

import com.work.pamper.entity.ActivityRegistration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ActivityRegistrationMapper {
    // 创建报名
    int createRegistration(ActivityRegistration registration);

    // 根据ID获取报名
    ActivityRegistration getRegistrationById(@Param("id") Long id);

    // 获取活动的报名列表
    List<ActivityRegistration> getRegistrationsByActivityId(@Param("activityId") Long activityId);

    // 获取用户的报名列表
    List<ActivityRegistration> getRegistrationsByUserId(@Param("userId") Long userId);

    // 检查用户是否已报名
    int checkUserRegistered(@Param("activityId") Long activityId, @Param("userId") Long userId);

    // 更新报名状态
    int updateRegistrationStatus(@Param("id") Long id, @Param("status") Integer status);

    // 删除报名
    int deleteRegistration(@Param("id") Long id);

    // 统计活动报名人数
    int countRegistrationsByActivityId(@Param("activityId") Long activityId, @Param("status") Integer status);
}
