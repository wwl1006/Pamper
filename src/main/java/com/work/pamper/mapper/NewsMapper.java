package com.work.pamper.mapper;

import com.work.pamper.entity.News;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NewsMapper {
    // 创建资讯
    int createNews(News news);

    // 根据ID获取资讯
    News getNewsById(@Param("id") Long id);

    // 获取资讯列表（分页）
    List<News> getNewsList(@Param("category") String category,
                           @Param("status") Integer status,
                           @Param("offset") int offset,
                           @Param("limit") int limit);

    // 获取资讯总数
    int getNewsCount(@Param("category") String category, @Param("status") Integer status);

    // 获取置顶资讯
    List<News> getTopNews(@Param("limit") int limit);

    // 更新资讯
    int updateNews(News news);

    // 删除资讯
    int deleteNews(@Param("id") Long id);

    // 更新资讯状态
    int updateNewsStatus(@Param("id") Long id, @Param("status") Integer status);

    // 设置置顶
    int updateNewsTop(@Param("id") Long id, @Param("isTop") Integer isTop);

    // 增加浏览次数
    int increaseViewCount(@Param("id") Long id);

    // 增加点赞次数
    int increaseLikeCount(@Param("id") Long id);
}
