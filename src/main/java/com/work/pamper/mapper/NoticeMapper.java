package com.work.pamper.mapper;

import com.work.pamper.entity.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoticeMapper {
    // 发送通知
    int sendNotice(Notice notice);

    // 获取用户收到的通知列表
    List<Notice> getNoticesByAccepter(@Param("receiver_id") Long receiver_id);

    // 获取所有公告
    List<Notice> getAllAnnouncements();

    // 根据ID获取通知
    Notice getNoticeById(@Param("id") Long id);

    // 获取用户发送的通知列表
    List<Notice> getNoticesBySender(@Param("sender_id") Long sender_id);
}
