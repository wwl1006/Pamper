package com.work.pamper.mapper;

import com.work.pamper.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {
    // 创建帖子
    int createPost(Post post);

    // 根据ID获取帖子
    Post getPostById(@Param("id") Long id);

    // 分页查询帖子列表（带用户信息）
    List<Post> getPostsWithPagination(
        @Param("category") String category,
        @Param("status") Integer status,
        @Param("offset") int offset,
        @Param("limit") int limit
    );

    // 统计帖子总数
    long countPosts(@Param("category") String category, @Param("status") Integer status);

    // 获取用户的帖子列表
    List<Post> getPostsByUserId(
        @Param("userId") Long userId,
        @Param("offset") int offset,
        @Param("limit") int limit
    );

    // 更新帖子状态
    int updatePostStatus(@Param("id") Long id, @Param("status") Integer status);

    // 增加浏览数
    int incrementViewCount(@Param("id") Long id);

    // 更新点赞数
    int updateLikeCount(@Param("id") Long id, @Param("increment") int increment);

    // 更新评论数
    int updateCommentCount(@Param("id") Long id, @Param("increment") int increment);

    // 删除帖子
    int deletePost(@Param("id") Long id);
}
