package com.work.pamper.mapper;

import com.work.pamper.entity.PostComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostCommentMapper {
    // 添加评论
    int addComment(PostComment comment);

    // 获取帖子的评论列表（带用户信息）
    List<PostComment> getCommentsByPostId(@Param("postId") Long postId);

    // 统计帖子的评论数
    long countCommentsByPostId(@Param("postId") Long postId);

    // 删除评论
    int deleteComment(@Param("id") Long id);
}
