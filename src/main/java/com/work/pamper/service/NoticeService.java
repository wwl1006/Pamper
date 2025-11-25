package com.work.pamper.service;

import com.work.pamper.entity.Notice;
import org.springframework.stereotype.Service;

@Service
public interface NoticeService {
    // 发送通知（私信或公告）
    Object sendNotice(String token, Notice notice);

    // 获取用户收到的通知列表
    Object getMyNotices(String token);

    // 获取所有公告
    Object getAllAnnouncements();

    // 根据ID获取通知详情
    Object getNoticeById(String token, Long id);

    // 获取用户发送的通知列表
    Object getMySentNotices(String token);
}
