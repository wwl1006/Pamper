package com.work.pamper.mapper;

import com.work.pamper.entity.Account;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper {
    Account getUserByUsername(String username);

    int saveUser(Account user);
}
