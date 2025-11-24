package com.work.pamper.service.impl;

import com.work.pamper.config.EnvConfig;
import com.work.pamper.dto.LoginResponse;
import com.work.pamper.entity.Account;
import com.work.pamper.mapper.AccountMapper;
import com.work.pamper.service.AccountService;
import com.work.pamper.utils.JwtUtils;
import com.work.pamper.utils.ResultUtil;
import com.work.pamper.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    AccountMapper accountMapper;
    private static final String UNAUTHORIZED_MSG = "登录状态已失效，请重新登录";

    private String trimTokenPrefix(String token) {
        if (token == null) {
            return null;
        }
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

    private Account sanitizeAccount(Account account) {
        if (account != null) {
            account.setPassword(null);
        }
        return account;
    }

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
            String now = TimeUtils.getCurrentTimeString();
            user.setCreate_time(now);
            user.setAvatar(EnvConfig.AvatarUrlPrefix + user.getUsername());
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
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setId(account.getId());
            loginResponse.setUsername(account.getUsername());
            loginResponse.setAvatar(account.getAvatar());
            loginResponse.setUser_type(account.getUser_type());
            loginResponse.setEmail(account.getEmail());
            loginResponse.setDescription(account.getDescription());
            loginResponse.setCreate_time(account.getCreate_time());
            String jwt = JwtUtils.createJwt(account.getId().toString(),3600000);
            loginResponse.setToken(jwt);
            return ResultUtil.success("登录成功", loginResponse);
        }
    }

    @Override
    public Object getProfileByToken(String token) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }
        Account account = accountMapper.getUserById(userId);
        if (account == null) {
            return ResultUtil.error("用户不存在");
        }
        return ResultUtil.success("获取成功", sanitizeAccount(account));
    }

    @Override
    public Object updateProfile(String token, Account profile) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }
        if (profile == null) {
            return ResultUtil.error("缺少需要更新的资料");
        }
        if ((profile.getEmail() == null || profile.getEmail().isEmpty())
                && (profile.getDescription() == null || profile.getDescription().isEmpty())
                && (profile.getAvatar() == null || profile.getAvatar().isEmpty())) {
            return ResultUtil.error("没有需要更新的内容");
        }
        profile.setId(userId);
        try {
            int result = accountMapper.updateProfile(profile);
            if (result > 0) {
                Account latest = accountMapper.getUserById(userId);
                return ResultUtil.success("资料更新成功", sanitizeAccount(latest));
            }
            return ResultUtil.error("资料更新失败");
        } catch (Exception e) {
            return ResultUtil.error("资料更新失败，发生异常：" + e.getMessage());
        }
    }

}
