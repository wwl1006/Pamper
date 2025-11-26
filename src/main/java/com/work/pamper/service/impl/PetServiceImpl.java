package com.work.pamper.service.impl;

import com.work.pamper.entity.PetProfile;
import com.work.pamper.entity.HealthRecord;
import com.work.pamper.mapper.PetProfileMapper;
import com.work.pamper.mapper.HealthRecordMapper;
import com.work.pamper.service.PetService;
import com.work.pamper.utils.JwtUtils;
import com.work.pamper.utils.ResultUtil;
import com.work.pamper.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PetServiceImpl implements PetService {
    @Autowired
    PetProfileMapper petProfileMapper;

    @Autowired
    HealthRecordMapper healthRecordMapper;

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
    public Object createPetProfile(String token, PetProfile petProfile) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        if (petProfile.getPet_name() == null || petProfile.getPet_name().trim().isEmpty()) {
            return ResultUtil.error("宠物名称不能为空");
        }

        petProfile.setUser_id(userId);
        petProfile.setCreate_time(TimeUtils.getCurrentTimeString());
        petProfile.setUpdate_time(TimeUtils.getCurrentTimeString());

        try {
            int result = petProfileMapper.createPetProfile(petProfile);
            if (result > 0) {
                return ResultUtil.success("添加成功", petProfile);
            } else {
                return ResultUtil.error("添加失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("添加失败：" + e.getMessage());
        }
    }

    @Override
    public Object getPetProfile(String token, Long petId) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            PetProfile pet = petProfileMapper.getPetProfileById(petId);
            if (pet == null) {
                return ResultUtil.error("宠物档案不存在");
            }

            if (!userId.equals(pet.getUser_id())) {
                return ResultUtil.error(403, "无权查看此宠物档案");
            }

            return ResultUtil.success("获取成功", pet);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败：" + e.getMessage());
        }
    }

    @Override
    public Object getMyPets(String token) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            List<PetProfile> pets = petProfileMapper.getPetsByUserId(userId);
            return ResultUtil.success("获取成功", pets);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Object updatePetProfile(String token, PetProfile petProfile) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            PetProfile existing = petProfileMapper.getPetProfileById(petProfile.getId());
            if (existing == null) {
                return ResultUtil.error("宠物档案不存在");
            }

            if (!userId.equals(existing.getUser_id())) {
                return ResultUtil.error(403, "无权修改此宠物档案");
            }

            petProfile.setUpdate_time(TimeUtils.getCurrentTimeString());
            int result = petProfileMapper.updatePetProfile(petProfile);
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
    public Object deletePetProfile(String token, Long petId) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            PetProfile pet = petProfileMapper.getPetProfileById(petId);
            if (pet == null) {
                return ResultUtil.error("宠物档案不存在");
            }

            if (!userId.equals(pet.getUser_id())) {
                return ResultUtil.error(403, "无权删除此宠物档案");
            }

            int result = petProfileMapper.deletePetProfile(petId);
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
    public Object addHealthRecord(String token, HealthRecord record) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            PetProfile pet = petProfileMapper.getPetProfileById(record.getPet_id());
            if (pet == null) {
                return ResultUtil.error("宠物档案不存在");
            }

            if (!userId.equals(pet.getUser_id())) {
                return ResultUtil.error(403, "无权为此宠物添加记录");
            }

            record.setCreate_time(TimeUtils.getCurrentTimeString());
            int result = healthRecordMapper.addHealthRecord(record);
            if (result > 0) {
                return ResultUtil.success("添加成功", record);
            } else {
                return ResultUtil.error("添加失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("添加失败：" + e.getMessage());
        }
    }

    @Override
    public Object getHealthRecords(String token, Long petId) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            PetProfile pet = petProfileMapper.getPetProfileById(petId);
            if (pet == null) {
                return ResultUtil.error("宠物档案不存在");
            }

            if (!userId.equals(pet.getUser_id())) {
                return ResultUtil.error(403, "无权查看此宠物的记录");
            }

            List<HealthRecord> records = healthRecordMapper.getRecordsByPetId(petId);
            return ResultUtil.success("获取成功", records);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Object updateHealthRecord(String token, HealthRecord record) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            HealthRecord existing = healthRecordMapper.getHealthRecordById(record.getId());
            if (existing == null) {
                return ResultUtil.error("健康记录不存在");
            }

            PetProfile pet = petProfileMapper.getPetProfileById(existing.getPet_id());
            if (!userId.equals(pet.getUser_id())) {
                return ResultUtil.error(403, "无权修改此记录");
            }

            int result = healthRecordMapper.updateHealthRecord(record);
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
    public Object deleteHealthRecord(String token, Long recordId) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            HealthRecord record = healthRecordMapper.getHealthRecordById(recordId);
            if (record == null) {
                return ResultUtil.error("健康记录不存在");
            }

            PetProfile pet = petProfileMapper.getPetProfileById(record.getPet_id());
            if (!userId.equals(pet.getUser_id())) {
                return ResultUtil.error(403, "无权删除此记录");
            }

            int result = healthRecordMapper.deleteHealthRecord(recordId);
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
}
