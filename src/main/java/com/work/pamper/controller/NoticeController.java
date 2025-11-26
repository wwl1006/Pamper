package com.work.pamper.controller;

import com.work.pamper.annotation.AutoControlLog;
import com.work.pamper.entity.Notice;
import com.work.pamper.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/notice")
@ResponseBody
@AutoControlLog(model = "通知模块")
public class NoticeController {
    @Autowired
    NoticeService noticeService;

    @RequestMapping(path = "/send", name = "发送通知接口", method = RequestMethod.POST)
    public Object sendNotice(@RequestHeader("Authorization") String token, @RequestBody Notice notice) {
        return noticeService.sendNotice(token, notice);
    }

    @RequestMapping(path = "/myNotices", name = "获取我的通知列表接口", method = RequestMethod.GET)
    public Object getMyNotices(@RequestHeader("Authorization") String token) {
        return noticeService.getMyNotices(token);
    }

    @RequestMapping(path = "/announcements", name = "获取公告列表接口", method = RequestMethod.GET)
    public Object getAllAnnouncements() {
        return noticeService.getAllAnnouncements();
    }

    @RequestMapping(path = "/{id:\\d+}", name = "获取通知详情接口", method = RequestMethod.GET)
    public Object getNoticeById(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        return noticeService.getNoticeById(token, id);
    }

    @RequestMapping(path = "/mySent", name = "获取我发送的通知列表接口", method = RequestMethod.GET)
    public Object getMySentNotices(@RequestHeader("Authorization") String token) {
        return noticeService.getMySentNotices(token);
    }
}
