package com.work.pamper.service.impl;

import com.work.pamper.entity.Account;
import com.work.pamper.mapper.AccountMapper;
import com.work.pamper.service.AccountService;
import com.work.pamper.utils.JwtUtils;
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
        if (user.getUsername() == null || user.getPassword() == null || user.getUser_type() == null) {
            return ResultUtil.error("用户名或密码和用户类型不能为空");
        }
        // 查看用户名是否存在
        Account account = accountMapper.getUserByUsername(user.getUsername());
        if (account == null) {
            return ResultUtil.error("用户名不存在");
        } else if (!account.getUser_type().equals(user.getUser_type())){
            return ResultUtil.error("用户类型错误");
        } else if (!account.getPassword().equals(user.getPassword())) {
            return ResultUtil.error("密码错误");
        } else {
            String jwt = JwtUtils.createJwt(account.getId().toString(),3600000);
            return ResultUtil.success("登录成功", jwt);
        }
    }

}
