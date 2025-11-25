package com.work.pamper.service.impl;

import com.work.pamper.entity.Post;
import com.work.pamper.entity.PostComment;
import com.work.pamper.entity.PostLike;
import com.work.pamper.entity.Account;
import com.work.pamper.mapper.PostMapper;
import com.work.pamper.mapper.PostLikeMapper;
import com.work.pamper.mapper.PostCommentMapper;
import com.work.pamper.mapper.AccountMapper;
import com.work.pamper.service.PostService;
import com.work.pamper.utils.JwtUtils;
import com.work.pamper.utils.ResultUtil;
import com.work.pamper.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    PostMapper postMapper;

    @Autowired
    PostLikeMapper postLikeMapper;

    @Autowired
    PostCommentMapper postCommentMapper;

    @Autowired
    AccountMapper accountMapper;

    private static final String UNAUTHORIZED_MSG = "登录状态已失效，请重新登录";

    // Token 解析方法（复用现有模式）
    private String trimTokenPrefix(String token) {
        if (token == null) return null;
        if (token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }

    private Long parseUserIdFromToken(String token) {
        String normalized = trimTokenPrefix(token);
        if (normalized == null || normalized.isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(JwtUtils.getSubject(normalized));
        } catch (Exception e) {
            return null;
        }
    }

    // 验证是否为管理员
    private boolean isAdmin(Long userId) {
        if (userId == null) return false;
        Account account = accountMapper.getUserById(userId);
        return account != null && Long.valueOf(0).equals(account.getUser_type());
    }

    @Override
    @Transactional
    public Object createPost(String token, Post post) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        // 参数验证
        if (post.getContent() == null || post.getContent().trim().isEmpty()) {
            return ResultUtil.error("帖子内容不能为空");
        }

        if (post.getPost_type() == null) {
            return ResultUtil.error("帖子类型不能为空");
        }

        // 设置默认值
        post.setUser_id(userId);
        post.setStatus(1);  // 先发布后审核
        post.setLike_count(0);
        post.setComment_count(0);
        post.setView_count(0);
        post.setCreate_time(TimeUtils.getCurrentTimeString());
        post.setUpdate_time(TimeUtils.getCurrentTimeString());

        if (post.getCategory() == null || post.getCategory().isEmpty()) {
            post.setCategory("其他");
        }

        try {
            int result = postMapper.createPost(post);
            if (result > 0) {
                return ResultUtil.success("发布成功", post);
            } else {
                return ResultUtil.error("发布失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("发布失败，发生异常：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Object getPostDetail(String token, Long postId) {
        if (postId == null) {
            return ResultUtil.error("帖子ID不能为空");
        }

        try {
            // 增加浏览数
            postMapper.incrementViewCount(postId);

            Post post = postMapper.getPostById(postId);
            if (post == null) {
                return ResultUtil.error("帖子不存在");
            }

            // 检查当前用户是否已点赞
            if (token != null) {
                Long userId = parseUserIdFromToken(token);
                if (userId != null) {
                    int liked = postLikeMapper.checkLike(postId, userId);
                    post.setIs_liked(liked > 0);
                }
            }

            return ResultUtil.success("获取成功", post);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败，发生异常：" + e.getMessage());
        }
    }

    @Override
    public Object getPostList(String token, String category, Integer page, Integer pageSize) {
        if (page == null || page < 1) page = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;

        try {
            int offset = (page - 1) * pageSize;

            // 只显示已发布的帖子
            List<Post> posts = postMapper.getPostsWithPagination(category, 1, offset, pageSize);
            long total = postMapper.countPosts(category, 1);

            // 批量检查点赞状态
            if (token != null) {
                Long userId = parseUserIdFromToken(token);
                if (userId != null) {
                    for (Post post : posts) {
                        int liked = postLikeMapper.checkLike(post.getId(), userId);
                        post.setIs_liked(liked > 0);
                    }
                }
            }

            Map<String, Object> data = new HashMap<>();
            data.put("list", posts);
            data.put("total", total);
            data.put("page", page);
            data.put("pageSize", pageSize);

            return ResultUtil.success("获取成功", data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败，发生异常：" + e.getMessage());
        }
    }

    @Override
    public Object getMyPosts(String token, Integer page, Integer pageSize) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        if (page == null || page < 1) page = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;

        try {
            int offset = (page - 1) * pageSize;
            List<Post> posts = postMapper.getPostsByUserId(userId, offset, pageSize);

            Map<String, Object> data = new HashMap<>();
            data.put("list", posts);
            data.put("page", page);
            data.put("pageSize", pageSize);

            return ResultUtil.success("获取成功", data);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败，发生异常：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Object toggleLike(String token, Long postId) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        if (postId == null) {
            return ResultUtil.error("帖子ID不能为空");
        }

        try {
            // 检查是否已点赞
            int liked = postLikeMapper.checkLike(postId, userId);

            if (liked > 0) {
                // 取消点赞
                postLikeMapper.removeLike(postId, userId);
                postMapper.updateLikeCount(postId, -1);
                return ResultUtil.success("已取消点赞");
            } else {
                // 添加点赞
                PostLike like = new PostLike();
                like.setPost_id(postId);
                like.setUser_id(userId);
                like.setCreate_time(TimeUtils.getCurrentTimeString());

                postLikeMapper.addLike(like);
                postMapper.updateLikeCount(postId, 1);
                return ResultUtil.success("点赞成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("操作失败，发生异常：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Object addComment(String token, PostComment comment) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        if (comment.getPost_id() == null) {
            return ResultUtil.error("帖子ID不能为空");
        }

        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            return ResultUtil.error("评论内容不能为空");
        }

        if (comment.getContent().length() > 500) {
            return ResultUtil.error("评论内容不能超过500字");
        }

        try {
            comment.setUser_id(userId);
            comment.setCreate_time(TimeUtils.getCurrentTimeString());

            int result = postCommentMapper.addComment(comment);
            if (result > 0) {
                // 更新帖子评论数
                postMapper.updateCommentCount(comment.getPost_id(), 1);
                return ResultUtil.success("评论成功");
            } else {
                return ResultUtil.error("评论失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("评论失败，发生异常：" + e.getMessage());
        }
    }

    @Override
    public Object getComments(Long postId) {
        if (postId == null) {
            return ResultUtil.error("帖子ID不能为空");
        }

        try {
            List<PostComment> comments = postCommentMapper.getCommentsByPostId(postId);
            return ResultUtil.success("获取成功", comments);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("获取失败，发生异常：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Object deletePost(String token, Long postId) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        if (postId == null) {
            return ResultUtil.error("帖子ID不能为空");
        }

        try {
            Post post = postMapper.getPostById(postId);
            if (post == null) {
                return ResultUtil.error("帖子不存在");
            }

            // 只能删除自己的帖子，或管理员可以删除任何帖子
            if (!userId.equals(post.getUser_id()) && !isAdmin(userId)) {
                return ResultUtil.error(403, "无权删除此帖子");
            }

            int result = postMapper.deletePost(postId);
            if (result > 0) {
                return ResultUtil.success("删除成功");
            } else {
                return ResultUtil.error("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("删除失败，发生异常：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Object updatePostStatus(String token, Long postId, Integer status) {
        Long userId = parseUserIdFromToken(token);
        if (userId == null) {
            return ResultUtil.error(401, UNAUTHORIZED_MSG);
        }

        // 验证管理员权限
        if (!isAdmin(userId)) {
            return ResultUtil.error(403, "权限不足，只有管理员才能审核帖子");
        }

        if (postId == null || status == null) {
            return ResultUtil.error("参数不完整");
        }

        try {
            int result = postMapper.updatePostStatus(postId, status);
            if (result > 0) {
                return ResultUtil.success("状态更新成功");
            } else {
                return ResultUtil.error("状态更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error("状态更新失败，发生异常：" + e.getMessage());
        }
    }
}
