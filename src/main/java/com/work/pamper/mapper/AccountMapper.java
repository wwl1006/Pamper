package com.work.pamper.mapper;

import com.work.pamper.entity.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AccountMapper {
    Account getUserByUsername(String username);

    int saveUser(Account user);
    Account getUserById(Long id);
    String getIdByUsername(String username);
    int updateProfile(Account account);
    Integer getUserType(Long id);

    // 管理员功能
    List<Map<String, Object>> getUserList(@Param("offset") int offset, @Param("limit") int limit);
    int getUserCount();
    int getUserCountByDate(@Param("date") String date);
    int getActiveUserCount();
    int updateUserStatus(@Param("userId") Long userId, @Param("status") Integer status);
}
