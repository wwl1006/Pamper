package com.work.pamper.controller;

import com.work.pamper.entity.Account;
import com.work.pamper.mapper.AccountMapper;
import com.work.pamper.service.AccountService;
import com.work.pamper.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//标记为Spring 的 Controller组件
//账号控制层
//允许跨域请求
//设置请求路径的前缀为 /account
@RestController
@CrossOrigin
@RequestMapping("/account")
@ResponseBody
public class AccountController {
    // 2025-11-17 今天完成的登录注册，有点过于顺利了，希望后续不会出什么问题 :)
    @Autowired
    AccountService accountService;
    @Autowired
    AccountMapper accountMapper;

    @RequestMapping("/login")
    public Object login(@RequestBody Account user) {
        return accountService.Login(user);
    }
    @RequestMapping("/register")
    public Object register(@RequestBody Account user) {
        return accountService.Register(user);
    }
}
