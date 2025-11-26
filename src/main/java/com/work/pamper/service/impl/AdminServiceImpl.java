package com.work.pamper.service.impl;

import com.work.pamper.mapper.*;
import com.work.pamper.service.AdminService;
import com.work.pamper.utils.JwtUtils;
import com.work.pamper.utils.ResultUtil;
import com.work.pamper.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AccountMapper accountMapper;

    @Autowired
    PostMapper postMapper;

    @Autowired
    AdoptionMapper adoptionMapper;

    @Autowired
    ActivityMapper activityMapper;

    @Autowired
    PetProfileMapper petProfileMapper;

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

    @Override
    public Object getUserList(Integer page, Integer pageSize) {
        try {
            if (page == null || page < 1) page = 1;
            if (pageSize == null || pageSize < 1) pageSize = 20;

            int offset = (page - 1) * pageSize;
            List<Map<String, Object>> users = accountMapper.getUserList(offset, pageSize);
            int total = accountMapper.getUserCount();

            Map<String, Object> data = new HashMap<>();
            data.put("list", users);
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
    public Object updateUserStatus(String token, Long userId, Integer status) {
        Long adminId = parseUserIdFromToken(token);
        if (adminId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        if (!isAdmin(adminId)) {
            return ResultUtil.error(403, "仅管理员可以操作");
        }

        try {
            int result = accountMapper.updateUserStatus(userId, status);
            if (result > 0) {
                return ResultUtil.success("操作成功");
            } else {
                return ResultUtil.error("操作失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("操作失败：" + e.getMessage());
        }
    }

    @Override
    public Object getPendingPosts(String token, Integer page, Integer pageSize) {
        Long adminId = parseUserIdFromToken(token);
        if (adminId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        if (!isAdmin(adminId)) {
            return ResultUtil.error(403, "仅管理员可以操作");
        }

        try {
            if (page == null || page < 1) page = 1;
            if (pageSize == null || pageSize < 1) pageSize = 20;

            int offset = (page - 1) * pageSize;
            List<Map<String, Object>> posts = postMapper.getPostsByStatus(0, offset, pageSize);
            int total = postMapper.countPostsByStatus(0);

            Map<String, Object> data = new HashMap<>();
            data.put("list", posts);
            data.put("total", total);

            return ResultUtil.success("获取成功", data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Object reviewPost(String token, Long postId, Integer status) {
        Long adminId = parseUserIdFromToken(token);
        if (adminId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        if (!isAdmin(adminId)) {
            return ResultUtil.error(403, "仅管理员可以操作");
        }

        try {
            int result = postMapper.updatePostStatus(postId, status);
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
    public Object getPendingAdoptions(String token, Integer page, Integer pageSize) {
        Long adminId = parseUserIdFromToken(token);
        if (adminId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        if (!isAdmin(adminId)) {
            return ResultUtil.error(403, "仅管理员可以操作");
        }

        try {
            if (page == null || page < 1) page = 1;
            if (pageSize == null || pageSize < 1) pageSize = 20;

            int offset = (page - 1) * pageSize;
            List<Map<String, Object>> adoptions = adoptionMapper.getAdoptionsByStatus(0, offset, pageSize);
            int total = adoptionMapper.countAdoptionsByStatus(0);

            Map<String, Object> data = new HashMap<>();
            data.put("list", adoptions);
            data.put("total", total);

            return ResultUtil.success("获取成功", data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Object reviewAdoption(String token, Long adoptionId, Integer status) {
        Long adminId = parseUserIdFromToken(token);
        if (adminId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        if (!isAdmin(adminId)) {
            return ResultUtil.error(403, "仅管理员可以操作");
        }

        try {
            int result = adoptionMapper.updateAdoptionStatus(adoptionId, status);
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
    public Object getPendingActivities(String token, Integer page, Integer pageSize) {
        Long adminId = parseUserIdFromToken(token);
        if (adminId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        if (!isAdmin(adminId)) {
            return ResultUtil.error(403, "仅管理员可以操作");
        }

        try {
            if (page == null || page < 1) page = 1;
            if (pageSize == null || pageSize < 1) pageSize = 20;

            int offset = (page - 1) * pageSize;
            List<Map<String, Object>> activities = activityMapper.getActivitiesByStatus(0, offset, pageSize);
            int total = activityMapper.getActivityCount(0, null);

            Map<String, Object> data = new HashMap<>();
            data.put("list", activities);
            data.put("total", total);

            return ResultUtil.success("获取成功", data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Object reviewActivity(String token, Long activityId, Integer status) {
        Long adminId = parseUserIdFromToken(token);
        if (adminId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        if (!isAdmin(adminId)) {
            return ResultUtil.error(403, "仅管理员可以操作");
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
    public Object getAllPosts(String token, Integer page, Integer pageSize) {
        Long adminId = parseUserIdFromToken(token);
        if (adminId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        if (!isAdmin(adminId)) {
            return ResultUtil.error(403, "仅管理员可以操作");
        }

        try {
            if (page == null || page < 1) page = 1;
            if (pageSize == null || pageSize < 1) pageSize = 20;

            int offset = (page - 1) * pageSize;
            List<Map<String, Object>> posts = postMapper.getAllPostsForAdmin(offset, pageSize);
            int total = postMapper.countAllPosts();

            Map<String, Object> data = new HashMap<>();
            data.put("list", posts);
            data.put("total", total);

            return ResultUtil.success("获取成功", data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败：" + e.getMessage());
        }
    }

    @Override
    public Object getAllAdoptions(String token, Integer page, Integer pageSize) {
        Long adminId = parseUserIdFromToken(token);
        if (adminId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        if (!isAdmin(adminId)) {
            return ResultUtil.error(403, "仅管理员可以操作");
        }

        try {
            if (page == null || page < 1) page = 1;
            if (pageSize == null || pageSize < 1) pageSize = 20;

            int offset = (page - 1) * pageSize;
            List<Map<String, Object>> adoptions = adoptionMapper.getAllAdoptionsForAdmin(offset, pageSize);
            int total = adoptionMapper.countAllAdoptions();

            Map<String, Object> data = new HashMap<>();
            data.put("list", adoptions);
            data.put("total", total);

            return ResultUtil.success("获取成功", data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败：" + e.getMessage());
        }
    }

    @Override
    public Object getAllActivities(String token, Integer page, Integer pageSize) {
        Long adminId = parseUserIdFromToken(token);
        if (adminId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        if (!isAdmin(adminId)) {
            return ResultUtil.error(403, "仅管理员可以操作");
        }

        try {
            if (page == null || page < 1) page = 1;
            if (pageSize == null || pageSize < 1) pageSize = 20;

            int offset = (page - 1) * pageSize;
            List<Map<String, Object>> activities = activityMapper.getAllActivitiesForAdmin(offset, pageSize);
            int total = activityMapper.countAllActivities();

            Map<String, Object> data = new HashMap<>();
            data.put("list", activities);
            data.put("total", total);

            return ResultUtil.success("获取成功", data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败：" + e.getMessage());
        }
    }

    @Override
    public Object getDashboardStats(String token) {
        Long adminId = parseUserIdFromToken(token);
        if (adminId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            Map<String, Object> stats = new HashMap<>();

            // 用户统计
            stats.put("totalUsers", accountMapper.getUserCount());
            stats.put("activeUsers", accountMapper.getActiveUserCount());

            // 内容统计
            stats.put("totalPosts", postMapper.countPostsByStatus(1));
            stats.put("pendingPosts", postMapper.countPostsByStatus(0));
            stats.put("totalAdoptions", adoptionMapper.countAdoptionsByStatus(1));
            stats.put("pendingAdoptions", adoptionMapper.countAdoptionsByStatus(0));

            // 活动统计
            stats.put("totalActivities", activityMapper.getActivityCount(1, null));
            stats.put("pendingActivities", activityMapper.getActivityCount(0, null));

            // 宠物档案统计
            stats.put("totalPets", petProfileMapper.getTotalPetCount());

            return ResultUtil.success("获取成功", stats);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败：" + e.getMessage());
        }
    }

    @Override
    public Object getUserGrowthData(String token, Integer days) {
        Long adminId = parseUserIdFromToken(token);
        if (adminId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            if (days == null || days < 1) days = 7;

            List<Map<String, Object>> data = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (int i = days - 1; i >= 0; i--) {
                LocalDate date = LocalDate.now().minusDays(i);
                String dateStr = date.format(formatter);

                Map<String, Object> item = new HashMap<>();
                item.put("date", dateStr);
                item.put("count", accountMapper.getUserCountByDate(dateStr));
                data.add(item);
            }

            return ResultUtil.success("获取成功", data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败：" + e.getMessage());
        }
    }

    @Override
    public Object getContentStats(String token) {
        Long adminId = parseUserIdFromToken(token);
        if (adminId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            Map<String, Object> stats = new HashMap<>();

            // 帖子分类统计
            stats.put("postsByCategory", postMapper.getPostCountByCategory());

            // 点赞和评论统计
            stats.put("totalLikes", postMapper.getTotalLikeCount());
            stats.put("totalComments", postMapper.getTotalCommentCount());

            // 领养宠物类型统计
            stats.put("adoptionsByPetType", adoptionMapper.getAdoptionCountByPetType());

            return ResultUtil.success("获取成功", stats);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败：" + e.getMessage());
        }
    }

    @Override
    public Object getActivityStats(String token) {
        Long adminId = parseUserIdFromToken(token);
        if (adminId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            Map<String, Object> stats = new HashMap<>();

            // 活动类型统计
            stats.put("activitiesByType", activityMapper.getActivityCountByType());

            // 报名统计
            stats.put("totalRegistrations", activityMapper.getTotalRegistrationCount());

            return ResultUtil.success("获取成功", stats);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败：" + e.getMessage());
        }
    }
}
