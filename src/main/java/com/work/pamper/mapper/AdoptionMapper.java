package com.work.pamper.mapper;

import com.work.pamper.entity.Adoption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdoptionMapper {
    // 创建领养信息
    int createAdoption(Adoption adoption);

    // 根据ID获取领养信息
    Adoption getAdoptionById(@Param("id") Long id);

    // 分页查询领养列表（带用户信息和申请数量）
    List<Adoption> getAdoptionsWithPagination(
        @Param("petType") String petType,
        @Param("status") Integer status,
        @Param("offset") int offset,
        @Param("limit") int limit
    );

    // 统计领养信息总数
    long countAdoptions(@Param("petType") String petType, @Param("status") Integer status);

    // 获取用户发布的领养列表
    List<Adoption> getAdoptionsByUserId(
        @Param("userId") Long userId,
        @Param("offset") int offset,
        @Param("limit") int limit
    );

    // 更新领养状态
    int updateAdoptionStatus(@Param("id") Long id, @Param("status") Integer status);

    // 删除领养信息
    int deleteAdoption(@Param("id") Long id);

    // 更新领养信息
    int updateAdoption(Adoption adoption);

    // 管理员功能
    @MapKey("id")
    List<Map<String, Object>> getAdoptionsByStatus(@Param("status") int status,
                                                   @Param("offset") int offset,
                                                   @Param("limit") int limit);
    int countAdoptionsByStatus(@Param("status") int status);
    @MapKey("pet_type")
    List<Map<String, Object>> getAdoptionCountByPetType();
    @MapKey("id")
    List<Map<String, Object>> getAllAdoptionsForAdmin(@Param("offset") int offset,
                                                       @Param("limit") int limit);
    int countAllAdoptions();
}
