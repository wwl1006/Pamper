-- 活动与咨询模块数据库表

-- 活动信息表
CREATE TABLE `activity` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '活动ID',
  `title` VARCHAR(200) NOT NULL COMMENT '活动标题',
  `content` TEXT NOT NULL COMMENT '活动内容详情',
  `cover_image` VARCHAR(500) COMMENT '封面图片URL',
  `images` TEXT COMMENT '活动图片列表，逗号分隔',
  `activity_type` VARCHAR(50) NOT NULL COMMENT '活动类型：线上/线下/混合',
  `location` VARCHAR(200) COMMENT '活动地点',
  `start_time` VARCHAR(30) NOT NULL COMMENT '开始时间',
  `end_time` VARCHAR(30) NOT NULL COMMENT '结束时间',
  `registration_deadline` VARCHAR(30) COMMENT '报名截止时间',
  `max_participants` INT DEFAULT 0 COMMENT '最大参与人数，0表示无限制',
  `current_participants` INT DEFAULT 0 COMMENT '当前报名人数',
  `organizer` VARCHAR(100) COMMENT '主办方',
  `contact` VARCHAR(100) COMMENT '联系方式',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0=待审核 1=已发布 2=已拒绝 3=已取消 4=进行中 5=已结束',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `create_by` BIGINT NOT NULL COMMENT '创建者ID',
  `create_time` VARCHAR(30) NOT NULL COMMENT '创建时间',
  `update_time` VARCHAR(30) NOT NULL COMMENT '更新时间',
  INDEX `idx_create_by` (`create_by`),
  INDEX `idx_status` (`status`),
  INDEX `idx_start_time` (`start_time`),
  INDEX `idx_activity_type` (`activity_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动信息表';

-- 活动报名表
CREATE TABLE `activity_registration` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '报名ID',
  `activity_id` BIGINT NOT NULL COMMENT '活动ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `real_name` VARCHAR(50) COMMENT '真实姓名',
  `phone` VARCHAR(20) COMMENT '联系电话',
  `email` VARCHAR(100) COMMENT '邮箱',
  `message` TEXT COMMENT '报名留言',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0=已取消 1=已报名 2=已签到',
  `create_time` VARCHAR(30) NOT NULL COMMENT '报名时间',
  `update_time` VARCHAR(30) COMMENT '更新时间',
  INDEX `idx_activity_id` (`activity_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_status` (`status`),
  UNIQUE KEY `uk_activity_user` (`activity_id`, `user_id`),
  FOREIGN KEY (`activity_id`) REFERENCES `activity`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动报名表';

-- 资讯信息表
CREATE TABLE `news` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '资讯ID',
  `title` VARCHAR(200) NOT NULL COMMENT '资讯标题',
  `summary` VARCHAR(500) COMMENT '摘要',
  `content` TEXT NOT NULL COMMENT '资讯内容',
  `cover_image` VARCHAR(500) COMMENT '封面图片URL',
  `category` VARCHAR(50) NOT NULL COMMENT '分类：养护知识、医疗健康、训练技巧、行业动态、其他',
  `tags` VARCHAR(200) COMMENT '标签，逗号分隔',
  `source` VARCHAR(100) COMMENT '来源',
  `author` VARCHAR(50) COMMENT '作者',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0=草稿 1=已发布 2=已下架',
  `is_top` TINYINT DEFAULT 0 COMMENT '是否置顶：0=否 1=是',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `like_count` INT DEFAULT 0 COMMENT '点赞次数',
  `create_by` BIGINT NOT NULL COMMENT '创建者ID',
  `create_time` VARCHAR(30) NOT NULL COMMENT '创建时间',
  `update_time` VARCHAR(30) NOT NULL COMMENT '更新时间',
  `publish_time` VARCHAR(30) COMMENT '发布时间',
  INDEX `idx_create_by` (`create_by`),
  INDEX `idx_category` (`category`),
  INDEX `idx_status` (`status`),
  INDEX `idx_is_top` (`is_top`),
  INDEX `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资讯信息表';
