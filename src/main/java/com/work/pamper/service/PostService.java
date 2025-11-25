package com.work.pamper.service;

import com.work.pamper.entity.Post;
import com.work.pamper.entity.PostComment;
import org.springframework.stereotype.Service;

@Service
public interface PostService {
    // 创建帖子
    Object createPost(String token, Post post);

    // 获取帖子详情
    Object getPostDetail(String token, Long postId);

    // 分页获取帖子列表
    Object getPostList(String token, String category, Integer page, Integer pageSize);

    // 获取用户的帖子列表
    Object getMyPosts(String token, Integer page, Integer pageSize);

    // 点赞/取消点赞
    Object toggleLike(String token, Long postId);

    // 添加评论
    Object addComment(String token, PostComment comment);

    // 获取评论列表
    Object getComments(Long postId);

    // 删除帖子
    Object deletePost(String token, Long postId);

    // 更新帖子状态（管理员）
    Object updatePostStatus(String token, Long postId, Integer status);
}
