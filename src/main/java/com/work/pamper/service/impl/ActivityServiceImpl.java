package com.work.pamper.service.impl;

import com.work.pamper.entity.Activity;
import com.work.pamper.entity.ActivityRegistration;
import com.work.pamper.entity.News;
import com.work.pamper.mapper.ActivityMapper;
import com.work.pamper.mapper.ActivityRegistrationMapper;
import com.work.pamper.mapper.NewsMapper;
import com.work.pamper.mapper.AccountMapper;
import com.work.pamper.service.ActivityService;
import com.work.pamper.utils.FileUtils;
import com.work.pamper.utils.JwtUtils;
import com.work.pamper.utils.ResultUtil;
import com.work.pamper.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActivityServiceImpl implements ActivityService {
    @Autowired
    ActivityMapper activityMapper;

    @Autowired
    ActivityRegistrationMapper registrationMapper;

    @Autowired
    NewsMapper newsMapper;

    @Autowired
    AccountMapper accountMapper;

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

    private boolean isAdmin(Long userId) {
        try {
            Integer userType = accountMapper.getUserType(userId);
            return userType != null && userType == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isMerchant(Long userId) {
        try {
            Integer userType = accountMapper.getUserType(userId);
            return userType != null && userType == 2;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean canCreateActivity(Long userId) {
        return isAdmin(userId) || isMerchant(userId);
    }

    @Override
    @Transactional
    public Object createActivity(String token, Activity activity) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        if (!canCreateActivity(userId)) {
            return ResultUtil.error(403, "仅管理员和商户用户可以创建活动");
        }

        if (activity.getTitle() == null || activity.getTitle().trim().isEmpty()) {
            return ResultUtil.error("活动标题不能为空");
        }

        // 确保 content 字段不为 null
        if (activity.getContent() == null) {
            activity.setContent("");
        }

        activity.setCreate_by(userId);
        activity.setCreate_time(TimeUtils.getCurrentTimeString());
        activity.setUpdate_time(TimeUtils.getCurrentTimeString());
        activity.setStatus(0);  // 待审核
        activity.setView_count(0);
        activity.setCurrent_participants(0);

        try {
            int result = activityMapper.createActivity(activity);
            if (result > 0) {
                return ResultUtil.success("创建成功", activity);
            } else {
                return ResultUtil.error("创建失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("创建失败：" + e.getMessage());
        }
    }

    @Override
    public Object getActivityDetail(String token, Long activityId) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            Activity activity = activityMapper.getActivityById(activityId, userId);
            if (activity == null) {
                return ResultUtil.error("活动不存在");
            }

            activityMapper.increaseViewCount(activityId);
            return ResultUtil.success("获取成功", activity);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败：" + e.getMessage());
        }
    }

    @Override
    public Object getActivityList(String activityType, Integer page, Integer pageSize, String token) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            userId = 0L;
        }

        try {
            if (page == null || page < 1) page = 1;
            if (pageSize == null || pageSize < 1) pageSize = 10;

            int offset = (page - 1) * pageSize;
            List<Activity> list = activityMapper.getActivityList(1, activityType, offset, pageSize, userId);
            int total = activityMapper.getActivityCount(1, activityType);

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
    public Object getMyActivities(String token) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            List<Activity> activities = activityMapper.getActivitiesByCreator(userId);
            return ResultUtil.success("获取成功", activities);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Object updateActivity(String token, Activity activity) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            Activity existing = activityMapper.getActivityById(activity.getId(), userId);
            if (existing == null) {
                return ResultUtil.error("活动不存在");
            }

            if (!userId.equals(existing.getCreate_by())) {
                return ResultUtil.error(403, "无权修改此活动");
            }

            activity.setUpdate_time(TimeUtils.getCurrentTimeString());
            int result = activityMapper.updateActivity(activity);
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
    public Object deleteActivity(String token, Long activityId) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            Activity activity = activityMapper.getActivityById(activityId, userId);
            if (activity == null) {
                return ResultUtil.error("活动不存在");
            }

            if (!userId.equals(activity.getCreate_by()) && !isAdmin(userId)) {
                return ResultUtil.error(403, "无权删除此活动");
            }

            int result = activityMapper.deleteActivity(activityId);
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
    public Object updateActivityStatus(String token, Long activityId, Integer status) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        if (!isAdmin(userId)) {
            return ResultUtil.error(403, "仅管理员可以审核活动");
        }

        try {
            int result = activityMapper.updateActivityStatus(activityId, status);
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
    public Object registerActivity(String token, ActivityRegistration registration) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            Activity activity = activityMapper.getActivityById(registration.getActivity_id(), userId);
            if (activity == null) {
                return ResultUtil.error("活动不存在");
            }

            if (activity.getStatus() != 1) {
                return ResultUtil.error("活动未发布，无法报名");
            }

            // 检查是否已报名
            int count = registrationMapper.checkUserRegistered(registration.getActivity_id(), userId);
            if (count > 0) {
                return ResultUtil.error("您已报名过此活动");
            }

            // 检查是否已满
            if (activity.getMax_participants() > 0 && activity.getCurrent_participants() >= activity.getMax_participants()) {
                return ResultUtil.error("活动已报满");
            }

            registration.setUser_id(userId);
            registration.setStatus(1);  // 直接报名成功
            registration.setCreate_time(TimeUtils.getCurrentTimeString());
            registration.setUpdate_time(TimeUtils.getCurrentTimeString());

            int result = registrationMapper.createRegistration(registration);
            if (result > 0) {
                activityMapper.increaseParticipants(registration.getActivity_id());
                return ResultUtil.success("报名成功", registration);
            } else {
                return ResultUtil.error("报名失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("报名失败：" + e.getMessage());
        }
    }

    @Override
    public Object getActivityRegistrations(String token, Long activityId) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            Activity activity = activityMapper.getActivityById(activityId, userId);
            if (activity == null) {
                return ResultUtil.error("活动不存在");
            }

            if (!userId.equals(activity.getCreate_by()) && !isAdmin(userId)) {
                return ResultUtil.error(403, "无权查看报名列表");
            }

            List<ActivityRegistration> registrations = registrationMapper.getRegistrationsByActivityId(activityId);
            return ResultUtil.success("获取成功", registrations);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败：" + e.getMessage());
        }
    }

    @Override
    public Object getMyRegistrations(String token) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            List<ActivityRegistration> registrations = registrationMapper.getRegistrationsByUserId(userId);
            return ResultUtil.success("获取成功", registrations);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Object cancelRegistration(String token, Long registrationId) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            ActivityRegistration registration = registrationMapper.getRegistrationById(registrationId);
            if (registration == null) {
                return ResultUtil.error("报名记录不存在");
            }

            if (!userId.equals(registration.getUser_id())) {
                return ResultUtil.error(403, "无权取消此报名");
            }

            int result = registrationMapper.updateRegistrationStatus(registrationId, 0);
            if (result > 0) {
                activityMapper.decreaseParticipants(registration.getActivity_id());
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
    public Object uploadActivityCover(String token, MultipartFile file) {
        return FileUtils.uploadActivityCover(file, token);
    }

    @Override
    @Transactional
    public Object createNews(String token, News news) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        if (!isAdmin(userId)) {
            return ResultUtil.error(403, "仅管理员可以发布资讯");
        }

        if (news.getTitle() == null || news.getTitle().trim().isEmpty()) {
            return ResultUtil.error("资讯标题不能为空");
        }

        news.setCreate_by(userId);
        news.setCreate_time(TimeUtils.getCurrentTimeString());
        news.setUpdate_time(TimeUtils.getCurrentTimeString());
        news.setPublish_time(TimeUtils.getCurrentTimeString());
        news.setStatus(1);  // 直接发布
        news.setIs_top(0);
        news.setView_count(0);
        news.setLike_count(0);

        try {
            int result = newsMapper.createNews(news);
            if (result > 0) {
                return ResultUtil.success("发布成功", news);
            } else {
                return ResultUtil.error("发布失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("发布失败：" + e.getMessage());
        }
    }

    @Override
    public Object getNewsDetail(Long newsId) {
        try {
            News news = newsMapper.getNewsById(newsId);
            if (news == null) {
                return ResultUtil.error("资讯不存在");
            }

            newsMapper.increaseViewCount(newsId);
            return ResultUtil.success("获取成功", news);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败：" + e.getMessage());
        }
    }

    @Override
    public Object getNewsList(String category, Integer page, Integer pageSize) {
        try {
            if (page == null || page < 1) page = 1;
            if (pageSize == null || pageSize < 1) pageSize = 10;

            int offset = (page - 1) * pageSize;
            List<News> list = newsMapper.getNewsList(category, 1, offset, pageSize);
            int total = newsMapper.getNewsCount(category, 1);

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
    @Transactional
    public Object updateNews(String token, News news) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            News existing = newsMapper.getNewsById(news.getId());
            if (existing == null) {
                return ResultUtil.error("资讯不存在");
            }

            if (!userId.equals(existing.getCreate_by()) && !isAdmin(userId)) {
                return ResultUtil.error(403, "无权修改此资讯");
            }

            news.setUpdate_time(TimeUtils.getCurrentTimeString());
            int result = newsMapper.updateNews(news);
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
    public Object deleteNews(String token, Long newsId) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        if (!isAdmin(userId)) {
            return ResultUtil.error(403, "仅管理员可以删除资讯");
        }

        try {
            int result = newsMapper.deleteNews(newsId);
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
    public Object updateNewsStatus(String token, Long newsId, Integer status) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        if (!isAdmin(userId)) {
            return ResultUtil.error(403, "仅管理员可以修改状态");
        }

        try {
            int result = newsMapper.updateNewsStatus(newsId, status);
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
    public Object updateNewsTop(String token, Long newsId, Integer isTop) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        if (!isAdmin(userId)) {
            return ResultUtil.error(403, "仅管理员可以设置置顶");
        }

        try {
            int result = newsMapper.updateNewsTop(newsId, isTop);
            if (result > 0) {
                return ResultUtil.success("设置成功");
            } else {
                return ResultUtil.error("设置失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("设置失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Object likeNews(Long newsId) {
        try {
            int result = newsMapper.increaseLikeCount(newsId);
            if (result > 0) {
                return ResultUtil.success("点赞成功");
            } else {
                return ResultUtil.error("点赞失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("点赞失败：" + e.getMessage());
        }
    }
}
