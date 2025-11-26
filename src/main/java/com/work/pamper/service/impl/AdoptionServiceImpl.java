package com.work.pamper.service.impl;

import com.work.pamper.entity.Adoption;
import com.work.pamper.entity.AdoptionApplication;
import com.work.pamper.entity.Account;
import com.work.pamper.mapper.AdoptionMapper;
import com.work.pamper.mapper.AdoptionApplicationMapper;
import com.work.pamper.mapper.AccountMapper;
import com.work.pamper.service.AdoptionService;
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
public class AdoptionServiceImpl implements AdoptionService {
    @Autowired
    AdoptionMapper adoptionMapper;

    @Autowired
    AdoptionApplicationMapper applicationMapper;

    @Autowired
    AccountMapper accountMapper;

    private static final String UNAUTHORIZED_MSG = "登录状态已失效，请重新登录";

    private String trimTokenPrefix(String token) {
        if (token == null) return null;
        if (token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }

    private Long parseUserIdFromToken(String token) {
        String normalized = trimTokenPrefix(token);
        if (normalized == null || normalized.isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(JwtUtils.getSubject(normalized));
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isAdmin(Long userId) {
        if (userId == null) return false;
        Account account = accountMapper.getUserById(userId);
        return account != null && Long.valueOf(0).equals(account.getUser_type());
    }

    @Override
    @Transactional
    public Object createAdoption(String token, Adoption adoption) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        // 参数验证
        if (adoption.getPet_name() == null || adoption.getPet_name().trim().isEmpty()) {
            return ResultUtil.error("宠物名称不能为空");
        }

        if (adoption.getPet_type() == null || adoption.getPet_type().trim().isEmpty()) {
            return ResultUtil.error("宠物类型不能为空");
        }

        // 设置默认值
        adoption.setUser_id(userId);
        adoption.setStatus(1);  // 先发布后审核
        adoption.setCreate_time(TimeUtils.getCurrentTimeString());
        adoption.setUpdate_time(TimeUtils.getCurrentTimeString());

        try {
            int result = adoptionMapper.createAdoption(adoption);
            if (result > 0) {
                return ResultUtil.success("发布成功", adoption);
            } else {
                return ResultUtil.error("发布失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("发布失败，发生异常：" + e.getMessage());
        }
    }

    @Override
    public Object getAdoptionDetail(Long adoptionId) {
        if (adoptionId == null) {
            return ResultUtil.error("领养ID不能为空");
        }

        try {
            Adoption adoption = adoptionMapper.getAdoptionById(adoptionId);
            if (adoption == null) {
                return ResultUtil.error("领养信息不存在");
            }

            return ResultUtil.success("获取成功", adoption);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败，发生异常：" + e.getMessage());
        }
    }

    @Override
    public Object getAdoptionList(String petType, Integer page, Integer pageSize) {
        if (page == null || page < 1) page = 1;
        if (pageSize == null || pageSize < 1) pageSize = 12;

        try {
            int offset = (page - 1) * pageSize;

            // 只显示已发布的领养信息
            List<Adoption> adoptions = adoptionMapper.getAdoptionsWithPagination(petType, 1, offset, pageSize);
            long total = adoptionMapper.countAdoptions(petType, 1);

            Map<String, Object> data = new HashMap<>();
            data.put("list", adoptions);
            data.put("total", total);
            data.put("page", page);
            data.put("pageSize", pageSize);

            return ResultUtil.success("获取成功", data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败，发生异常：" + e.getMessage());
        }
    }

    @Override
    public Object getMyAdoptions(String token, Integer page, Integer pageSize) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        if (page == null || page < 1) page = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;

        try {
            int offset = (page - 1) * pageSize;
            List<Adoption> adoptions = adoptionMapper.getAdoptionsByUserId(userId, offset, pageSize);

            Map<String, Object> data = new HashMap<>();
            data.put("list", adoptions);
            data.put("page", page);
            data.put("pageSize", pageSize);

            return ResultUtil.success("获取成功", data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败，发生异常：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Object updateAdoptionStatus(String token, Long adoptionId, Integer status) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        if (adoptionId == null || status == null) {
            return ResultUtil.error("参数不完整");
        }

        try {
            Adoption adoption = adoptionMapper.getAdoptionById(adoptionId);
            if (adoption == null) {
                return ResultUtil.error("领养信息不存在");
            }

            // 只有发布者或管理员可以修改状态
            if (!userId.equals(adoption.getUser_id()) && !isAdmin(userId)) {
                return ResultUtil.error(403, "无权修改此领养信息");
            }

            int result = adoptionMapper.updateAdoptionStatus(adoptionId, status);
            if (result > 0) {
                return ResultUtil.success("状态更新成功");
            } else {
                return ResultUtil.error("状态更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("状态更新失败，发生异常：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Object deleteAdoption(String token, Long adoptionId) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        if (adoptionId == null) {
            return ResultUtil.error("领养ID不能为空");
        }

        try {
            Adoption adoption = adoptionMapper.getAdoptionById(adoptionId);
            if (adoption == null) {
                return ResultUtil.error("领养信息不存在");
            }

            // 只能删除自己的领养信息，或管理员可以删除任何
            if (!userId.equals(adoption.getUser_id()) && !isAdmin(userId)) {
                return ResultUtil.error(403, "无权删除此领养信息");
            }

            int result = adoptionMapper.deleteAdoption(adoptionId);
            if (result > 0) {
                return ResultUtil.success("删除成功");
            } else {
                return ResultUtil.error("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("删除失败，发生异常：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Object applyAdoption(String token, AdoptionApplication application) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        if (application.getAdoption_id() == null) {
            return ResultUtil.error("领养ID不能为空");
        }

        try {
            // 检查领养信息是否存在
            Adoption adoption = adoptionMapper.getAdoptionById(application.getAdoption_id());
            if (adoption == null) {
                return ResultUtil.error("领养信息不存在");
            }

            // 检查是否已申请
            int existed = applicationMapper.checkApplication(application.getAdoption_id(), userId);
            if (existed > 0) {
                return ResultUtil.error("您已经申请过了");
            }

            // 不能申请自己发布的领养信息
            if (userId.equals(adoption.getUser_id())) {
                return ResultUtil.error("不能申请自己发布的领养信息");
            }

            application.setApplicant_id(userId);
            application.setStatus(0);  // 待审核
            application.setCreate_time(TimeUtils.getCurrentTimeString());
            application.setUpdate_time(TimeUtils.getCurrentTimeString());

            int result = applicationMapper.createApplication(application);
            if (result > 0) {
                return ResultUtil.success("申请提交成功");
            } else {
                return ResultUtil.error("申请提交失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("申请失败，发生异常：" + e.getMessage());
        }
    }

    @Override
    public Object getApplicationsByAdoption(String token, Long adoptionId) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        if (adoptionId == null) {
            return ResultUtil.error("领养ID不能为空");
        }

        try {
            // 验证是否是发布者
            Adoption adoption = adoptionMapper.getAdoptionById(adoptionId);
            if (adoption == null) {
                return ResultUtil.error("领养信息不存在");
            }

            if (!userId.equals(adoption.getUser_id())) {
                return ResultUtil.error(403, "无权查看此领养信息的申请列表");
            }

            List<AdoptionApplication> applications = applicationMapper.getApplicationsByAdoptionId(adoptionId);
            return ResultUtil.success("获取成功", applications);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败，发生异常：" + e.getMessage());
        }
    }

    @Override
    public Object getMyApplications(String token, Integer page, Integer pageSize) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        if (page == null || page < 1) page = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;

        try {
            int offset = (page - 1) * pageSize;
            List<AdoptionApplication> applications = applicationMapper.getApplicationsByApplicantId(userId, offset, pageSize);

            Map<String, Object> data = new HashMap<>();
            data.put("list", applications);
            data.put("page", page);
            data.put("pageSize", pageSize);

            return ResultUtil.success("获取成功", data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败，发生异常：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Object reviewApplication(String token, Long applicationId, Integer status) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        if (applicationId == null || status == null) {
            return ResultUtil.error("参数不完整");
        }

        try {
            AdoptionApplication application = applicationMapper.getApplicationById(applicationId);
            if (application == null) {
                return ResultUtil.error("申请不存在");
            }

            // 验证是否是发布者
            Adoption adoption = adoptionMapper.getAdoptionById(application.getAdoption_id());
            if (adoption == null) {
                return ResultUtil.error("领养信息不存在");
            }

            if (!userId.equals(adoption.getUser_id())) {
                return ResultUtil.error(403, "无权审核此申请");
            }

            int result = applicationMapper.updateApplicationStatus(applicationId, status);
            if (result > 0) {
                // 如果通过申请，将领养信息状态改为已领养
                if (status == 1) {
                    adoptionMapper.updateAdoptionStatus(adoption.getId(), 3);
                }
                return ResultUtil.success("审核成功");
            } else {
                return ResultUtil.error("审核失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("审核失败，发生异常：" + e.getMessage());
        }
    }
}
