package com.work.pamper.controller;

import com.work.pamper.annotation.AutoControlLog;
import com.work.pamper.entity.Post;
import com.work.pamper.entity.PostComment;
import com.work.pamper.service.PostService;
import com.work.pamper.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
@RequestMapping("/post")
@ResponseBody
@AutoControlLog(model = "帖子模块")
public class PostController {
    @Autowired
    PostService postService;

    // 创建帖子
    @RequestMapping(path = "/create", name = "创建帖子接口", method = RequestMethod.POST)
    public Object createPost(@RequestHeader("token") String token, @RequestBody Post post) {
        return postService.createPost(token, post);
    }

    // 获取帖子详情
    @RequestMapping(path = "/{id}", name = "获取帖子详情接口", method = RequestMethod.GET)
    public Object getPostDetail(@RequestHeader(value = "token", required = false) String token,
                               @PathVariable Long id) {
        return postService.getPostDetail(token, id);
    }

    // 获取帖子列表
    @RequestMapping(path = "/list", name = "获取帖子列表接口", method = RequestMethod.GET)
    public Object getPostList(@RequestHeader(value = "token", required = false) String token,
                             @RequestParam(required = false) String category,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        return postService.getPostList(token, category, page, pageSize);
    }

    // 获取我的帖子列表
    @RequestMapping(path = "/my", name = "获取我的帖子接口", method = RequestMethod.GET)
    public Object getMyPosts(@RequestHeader("token") String token,
                            @RequestParam(defaultValue = "1") Integer page,
                            @RequestParam(defaultValue = "10") Integer pageSize) {
        return postService.getMyPosts(token, page, pageSize);
    }

    // 点赞/取消点赞
    @RequestMapping(path = "/like/{id}", name = "点赞帖子接口", method = RequestMethod.POST)
    public Object toggleLike(@RequestHeader("token") String token, @PathVariable Long id) {
        return postService.toggleLike(token, id);
    }

    // 添加评论
    @RequestMapping(path = "/comment", name = "添加评论接口", method = RequestMethod.POST)
    public Object addComment(@RequestHeader("token") String token, @RequestBody PostComment comment) {
        return postService.addComment(token, comment);
    }

    // 获取评论列表
    @RequestMapping(path = "/{id}/comments", name = "获取评论列表接口", method = RequestMethod.GET)
    public Object getComments(@PathVariable Long id) {
        return postService.getComments(id);
    }

    // 删除帖子
    @RequestMapping(path = "/{id}", name = "删除帖子接口", method = RequestMethod.DELETE)
    public Object deletePost(@RequestHeader("token") String token, @PathVariable Long id) {
        return postService.deletePost(token, id);
    }

    // 更新帖子状态（管理员）
    @RequestMapping(path = "/{id}/status", name = "更新帖子状态接口", method = RequestMethod.PUT)
    public Object updatePostStatus(@RequestHeader("token") String token,
                                  @PathVariable Long id,
                                  @RequestParam Integer status) {
        return postService.updatePostStatus(token, id, status);
    }

    // 上传帖子图片
    @RequestMapping(path = "/upload/images", name = "上传帖子图片接口", method = RequestMethod.POST)
    public Object uploadImages(@RequestHeader("token") String token,
                              @RequestParam("files") MultipartFile[] files) {
        return FileUtils.uploadPostImages(files, token);
    }

    // 上传帖子视频
    @RequestMapping(path = "/upload/video", name = "上传帖子视频接口", method = RequestMethod.POST)
    public Object uploadVideo(@RequestHeader("token") String token,
                             @RequestParam("file") MultipartFile file) {
        return FileUtils.uploadPostVideo(file, token);
    }

    // 获取帖子图片
    @GetMapping("/image/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) {
        return FileUtils.showPostImage(filename);
    }

    // 获取帖子视频
    @GetMapping("/video/{filename}")
    public ResponseEntity<byte[]> getVideo(@PathVariable String filename) {
        return FileUtils.showPostVideo(filename);
    }
}
