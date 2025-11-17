package com.work.pamper.service.impl;

import com.work.pamper.entity.Account;
import com.work.pamper.mapper.AccountMapper;
import com.work.pamper.service.AccountService;
import com.work.pamper.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    AccountMapper accountMapper;
    @Override
    public Object Register(Account user) {
        if (user.getUsername() == null || user.getPassword() == null || user.getUser_type() == null) {
            return ResultUtil.error("用户名或密码和用户类型不能为空");
        }
        // 查看用户名是否存在
        Account account = accountMapper.getUserByUsername(user.getUsername());

        if (account != null) {
            // 如果用户名已存在，则返回错误信息
            return ResultUtil.error("用户名已存在");
        } else {
            // 如果用户名不存在，尝试保存用户信息
            try {
                int result = accountMapper.saveUser(user);

                // 检查是否保存成功
                if (result > 0) {
                    return ResultUtil.success("注册成功");
                } else {
                    return ResultUtil.error("注册失败");
                }
            } catch (Exception e) {
                // 捕获异常并返回错误信息
                return ResultUtil.error("注册失败，发生异常：" + e.getMessage());
            }
        }
    }


    @Override
    public Object Login(Account user) {
        // TODO: Implement login logic here
        // 查看用户名是否存在
        return null;
    }

}
