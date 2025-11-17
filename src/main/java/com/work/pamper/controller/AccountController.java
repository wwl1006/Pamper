package com.work.pamper.controller;

import com.work.pamper.service.AccountService;
import com.work.pamper.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//标记为Spring 的 Controller组件
//账号控制层
//允许跨域请求
//设置请求路径的前缀为 /account
@RestController
@CrossOrigin
@RequestMapping("/account")
public class AccountController {
    @Autowired
    AccountService accountService;

    @RequestMapping("/login")
    public Object login() {
        return ResultUtil.success("登录成功");
    }
}
