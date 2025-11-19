package com.work.pamper.controller;

import com.work.pamper.entity.Follow;
import com.work.pamper.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/fans")
@ResponseBody
public class FollowController {
    @Autowired
    FollowService followService;
    @RequestMapping(path = "/follow", name = "用户关注接口")
    public Object follow(@RequestBody Follow follow) {
        return followService.follow(follow);
    }
}
