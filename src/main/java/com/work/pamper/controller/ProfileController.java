package com.work.pamper.controller;

import ch.qos.logback.core.util.FileUtil;
import com.work.pamper.annotation.AutoControlLog;
import com.work.pamper.entity.Profile;
import com.work.pamper.utils.FileUtils;
import com.work.pamper.utils.ResultUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
@RequestMapping("/profile")
@ResponseBody
@AutoControlLog(model = "用户模块")
public class ProfileController {
    @RequestMapping(path = "/uploadAvatar", name = "用户头像上传接口")
    public Object uploadAvatar(@RequestHeader("token") String token,@RequestParam("file") MultipartFile file) {
        return FileUtils.uploadAvatar(file,token);
    }

    @RequestMapping(path = "/update", name = "用户资料更新接口")
    public Object profile(@RequestBody Profile profile) {
        // 暂未接入数据库，直接回显
        return ResultUtil.success("更新成功", profile);
    }

    @RequestMapping(path = "/get", name = "用户资料获取接口")
    public Object getProfile() {
        // 暂未接入数据库，返回一个示例数据
        Profile demo = new Profile();
        demo.setUsername("demo_user");
        demo.setEmail("demo@example.com");
        demo.setDescription("这是一个示例用户资料");
        return ResultUtil.success(demo);
    }
}
