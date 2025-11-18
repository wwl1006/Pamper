package com.work.pamper.service.impl;

import com.work.pamper.entity.Follow;
import com.work.pamper.mapper.FollowMapper;
import com.work.pamper.service.FollowService;
import com.work.pamper.utils.ResultUtil;
import com.work.pamper.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FollowServiceImpl implements FollowService {
    @Autowired
    FollowMapper followMapper;

    @Override
    public Object follow(Follow follow) {
        // 检查是否已经关注过了
        int info = followMapper.checkFollow(follow);
        if (info > 0) {
            return ResultUtil.error("您已关注该用户，无需重复关注");
        }
        try{
            String now = TimeUtils.getCurrentTimeString();
            follow.setCreate_time(now);
            int result = followMapper.follow(follow);
            if (result > 0) {
                return ResultUtil.success("关注成功");
            } else {
                return ResultUtil.error("关注失败");
            }
        } catch (Exception e) {
            return ResultUtil.error("关注失败，发生异常：" + e.getMessage());
        }
    }
}
