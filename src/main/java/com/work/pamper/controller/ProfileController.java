package com.work.pamper.controller;

import com.work.pamper.annotation.AutoControlLog;
import com.work.pamper.entity.Account;
import com.work.pamper.mapper.AccountMapper;
import com.work.pamper.service.AccountService;
import com.work.pamper.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

@RestController
@CrossOrigin
@RequestMapping("/profile")
@AutoControlLog(model = "用户模块")
public class ProfileController {

    @Autowired
    AccountMapper accountMapper;
    @Autowired
    AccountService accountService;

    @GetMapping("/me")
    public Object profile(@RequestHeader("token") String token) {
        return accountService.getProfileByToken(token);
    }

    @PutMapping("/me")
    public Object updateProfile(@RequestHeader("token") String token,
                                @RequestBody Account profile) {
        return accountService.updateProfile(token, profile);
    }

    @RequestMapping(path = "/uploadAvatar", name = "用户头像上传接口")
    public Object uploadAvatar(@RequestHeader("token") String token, @RequestParam("file") MultipartFile file) {
        return FileUtils.uploadAvatar(file, token);
    }

    @GetMapping("/avatar")
    public ResponseEntity<byte[]> getAvatar(@RequestHeader("token") String token) {
        return FileUtils.showAvatar(token);
    }

    @GetMapping("/avatar/id/{id}")
    public ResponseEntity<byte[]> getAvatarById(@PathVariable("id") String id) {
        return FileUtils.showAvatarById(id);
    }

    // 根据UserName获取
    @GetMapping("/avatar/username/{username}")
    public ResponseEntity<byte[]> getAvatarByUsername(@PathVariable("username") String username) {
        return FileUtils.showAvatarById(accountMapper.getIdByUsername(username));
    }
}
