package com.work.pamper.mapper;

import com.work.pamper.entity.PostLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PostLikeMapper {
    // 添加点赞
    int addLike(PostLike like);

    // 取消点赞
    int removeLike(@Param("postId") Long postId, @Param("userId") Long userId);

    // 检查是否已点赞
    int checkLike(@Param("postId") Long postId, @Param("userId") Long userId);
}
