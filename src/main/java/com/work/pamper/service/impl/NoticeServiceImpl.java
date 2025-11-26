package com.work.pamper.service.impl;

import com.work.pamper.entity.Notice;
import com.work.pamper.entity.Account;
import com.work.pamper.mapper.NoticeMapper;
import com.work.pamper.mapper.AccountMapper;
import com.work.pamper.service.NoticeService;
import com.work.pamper.utils.JwtUtils;
import com.work.pamper.utils.ResultUtil;
import com.work.pamper.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {
    @Autowired
    NoticeMapper noticeMapper;

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

    // 新增：验证是否为管理员
    private boolean isAdmin(Long userId) {
        if (userId == null) {
            return false;
        }
        Account account = accountMapper.getUserById(userId);
        return account != null && Integer.valueOf(0).equals(account.getUser_type());
    }

    @Override
    public Object sendNotice(String token, Notice notice) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        if (notice == null || notice.getMessage() == null || notice.getMessage().isEmpty()) {
            return ResultUtil.error("消息内容不能为空");
        }

        if (notice.getNotice_type() == null) {
            return ResultUtil.error("通知类型不能为空");
        }

        // 设置发送者为当前用户
        notice.setSender_id(userId);

        // 私信处理（notice_type = 0）
        if (notice.getNotice_type() == 0) {
            // 检查是否提供了用户名
            if (notice.getReceiverUsername() == null || notice.getReceiverUsername().isEmpty()) {
                return ResultUtil.error("私信需要指定接收者用户名");
            }

            // 根据用户名查询用户ID
            String receiverIdStr = accountMapper.getIdByUsername(notice.getReceiverUsername());
            if (receiverIdStr == null) {
                return ResultUtil.error("接收者用户名不存在");
            }

            try {
                Long receiverId = Long.valueOf(receiverIdStr);
                notice.setReceiver_id(receiverId);
            } catch (NumberFormatException e) {
                return ResultUtil.error("用户ID格式错误");
            }
        }

        // 公告处理（notice_type = 1）
        if (notice.getNotice_type() == 1) {
            // 验证管理员权限
            if (!isAdmin(userId)) {
                return ResultUtil.error(403, "权限不足，只有管理员才能发送公告");
            }
            // 公告的接收者为空
            notice.setReceiver_id(null);
        }

        try {
            String now = TimeUtils.getCurrentTimeString();
            notice.setCreate_time(now);
            int result = noticeMapper.sendNotice(notice);
            if (result > 0) {
                return ResultUtil.success("发送成功");
            } else {
                return ResultUtil.error("发送失败");
            }
        } catch (Exception e) {
            return ResultUtil.error("发送失败，发生异常：" + e.getMessage());
        }
    }

    @Override
    public Object getMyNotices(String token) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            List<Notice> notices = noticeMapper.getNoticesByAccepter(userId);
            return ResultUtil.success("获取成功", notices);
        } catch (Exception e) {
            return ResultUtil.error("获取失败，发生异常：" + e.getMessage());
        }
    }

    @Override
    public Object getAllAnnouncements() {
        try {
            List<Notice> announcements = noticeMapper.getAllAnnouncements();
            return ResultUtil.success("获取成功", announcements);
        } catch (Exception e) {
            return ResultUtil.error("获取失败，发生异常：" + e.getMessage());
        }
    }

    @Override
    public Object getNoticeById(String token, Long id) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        if (id == null) {
            return ResultUtil.error("通知ID不能为空");
        }

        try {
            Notice notice = noticeMapper.getNoticeById(id);
            if (notice == null) {
                return ResultUtil.error("通知不存在");
            }

            // 检查权限：只能查看自己收到的私信或公告
            if (notice.getNotice_type() == 0) {
                if (!userId.equals(notice.getReceiver_id()) && !userId.equals(notice.getSender_id())) {
                    return ResultUtil.error("无权查看此通知");
                }
            }

            return ResultUtil.success("获取成功", notice);
        } catch (Exception e) {
            return ResultUtil.error("获取失败，发生异常：" + e.getMessage());
        }
    }

    @Override
    public Object getMySentNotices(String token) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        try {
            List<Notice> notices = noticeMapper.getNoticesBySender(userId);
            return ResultUtil.success("获取成功", notices);
        } catch (Exception e) {
            return ResultUtil.error("获取失败，发生异常：" + e.getMessage());
        }
    }
}
