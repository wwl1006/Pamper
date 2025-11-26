package com.work.pamper.controller;

import com.work.pamper.entity.Activity;
import com.work.pamper.entity.ActivityRegistration;
import com.work.pamper.entity.News;
import com.work.pamper.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.work.pamper.utils.FileUtils;

@RestController
@CrossOrigin
@RequestMapping("/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    // 活动管理
    @PostMapping("/create")
    public Object createActivity(@RequestHeader("Authorization") String token,
                                 @RequestBody Activity activity) {
        return activityService.createActivity(token, activity);
    }

    @PostMapping("/upload/cover")
    public Object uploadCover(@RequestHeader("Authorization") String token,
                              @RequestParam("file") MultipartFile file) {
        return activityService.uploadActivityCover(token, file);
    }

    // 获取活动封面
    @GetMapping("/cover/{filename}")
    public ResponseEntity<byte[]> getCover(@PathVariable String filename) {
        return FileUtils.showActivityCover(filename);
    }

    @GetMapping("/{id:\\d+}")
    public Object getActivityDetail(@RequestHeader("Authorization") String token,
                                   @PathVariable Long id) {
        return activityService.getActivityDetail(token, id);
    }

    @GetMapping("/list")
    public Object getActivityList(@RequestParam(required = false) String activityType,
                                 @RequestParam(defaultValue = "1") Integer page,
                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                 @RequestHeader(value = "Authorization", required = false) String token) {
        return activityService.getActivityList(activityType, page, pageSize, token);
    }

    @GetMapping("/my")
    public Object getMyActivities(@RequestHeader("Authorization") String token) {
        return activityService.getMyActivities(token);
    }

    @PutMapping("/update")
    public Object updateActivity(@RequestHeader("Authorization") String token,
                                @RequestBody Activity activity) {
        return activityService.updateActivity(token, activity);
    }

    @DeleteMapping("/{id:\\d+}")
    public Object deleteActivity(@RequestHeader("Authorization") String token,
                                @PathVariable Long id) {
        return activityService.deleteActivity(token, id);
    }

    @PutMapping("/status")
    public Object updateActivityStatus(@RequestHeader("Authorization") String token,
                                      @RequestParam Long activityId,
                                      @RequestParam Integer status) {
        return activityService.updateActivityStatus(token, activityId, status);
    }

    // 活动报名
    @PostMapping("/register")
    public Object registerActivity(@RequestHeader("Authorization") String token,
                                  @RequestBody ActivityRegistration registration) {
        return activityService.registerActivity(token, registration);
    }

    @GetMapping("/registrations/{activityId:\\d+}")
    public Object getActivityRegistrations(@RequestHeader("Authorization") String token,
                                          @PathVariable Long activityId) {
        return activityService.getActivityRegistrations(token, activityId);
    }

    @GetMapping("/registrations/my")
    public Object getMyRegistrations(@RequestHeader("Authorization") String token) {
        return activityService.getMyRegistrations(token);
    }

    @PutMapping("/registration/cancel/{id:\\d+}")
    public Object cancelRegistration(@RequestHeader("Authorization") String token,
                                    @PathVariable Long id) {
        return activityService.cancelRegistration(token, id);
    }

    // 资讯管理
    @PostMapping("/news/create")
    public Object createNews(@RequestHeader("Authorization") String token,
                            @RequestBody News news) {
        return activityService.createNews(token, news);
    }

    @GetMapping("/news/{id:\\d+}")
    public Object getNewsDetail(@PathVariable Long id) {
        return activityService.getNewsDetail(id);
    }

    @GetMapping("/news/list")
    public Object getNewsList(@RequestParam(required = false) String category,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        return activityService.getNewsList(category, page, pageSize);
    }

    @PutMapping("/news/update")
    public Object updateNews(@RequestHeader("Authorization") String token,
                            @RequestBody News news) {
        return activityService.updateNews(token, news);
    }

    @DeleteMapping("/news/{id:\\d+}")
    public Object deleteNews(@RequestHeader("Authorization") String token,
                            @PathVariable Long id) {
        return activityService.deleteNews(token, id);
    }

    @PutMapping("/news/status")
    public Object updateNewsStatus(@RequestHeader("Authorization") String token,
                                   @RequestParam Long newsId,
                                   @RequestParam Integer status) {
        return activityService.updateNewsStatus(token, newsId, status);
    }

    @PutMapping("/news/top")
    public Object updateNewsTop(@RequestHeader("Authorization") String token,
                               @RequestParam Long newsId,
                               @RequestParam Integer isTop) {
        return activityService.updateNewsTop(token, newsId, isTop);
    }

    @PostMapping("/news/like/{id:\\d+}")
    public Object likeNews(@PathVariable Long id) {
        return activityService.likeNews(id);
    }
}
