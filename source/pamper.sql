/*
 Pamper - 宠物交流与服务平台数据库

 Date: 2025-11-26
 Version: 2.0
 Description: 完整的数据库表结构定义，统一规范化设计
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ===========================
-- 用户账户模块
-- ===========================

-- 账户表
DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `user_type` TINYINT NOT NULL DEFAULT 0 COMMENT '用户类型：0=普通用户 1=管理员',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '个人简介',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0=禁用 1=正常',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_status` (`status`),
  KEY `idx_user_type` (`user_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户表';

-- 关注表
DROP TABLE IF EXISTS `follow`;
CREATE TABLE `follow` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关注ID',
  `follower_id` BIGINT NOT NULL COMMENT '关注者ID',
  `followed_id` BIGINT NOT NULL COMMENT '被关注者ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_follower_followed` (`follower_id`, `followed_id`),
  KEY `idx_follower` (`follower_id`),
  KEY `idx_followed` (`followed_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='关注表';

-- 通知表
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `notice_type` TINYINT NOT NULL COMMENT '通知类型：0=私信 1=系统公告',
  `sender_id` BIGINT DEFAULT NULL COMMENT '发送者ID',
  `receiver_id` BIGINT COMMENT '接收者ID',
  `message` VARCHAR(500) NOT NULL COMMENT '通知内容',
  `is_read` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读：0=未读 1=已读',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_receiver` (`receiver_id`),
  KEY `idx_sender` (`sender_id`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- 操作日志表
DROP TABLE IF EXISTS `action_log`;
CREATE TABLE `action_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `account_id` BIGINT NOT NULL COMMENT '用户ID',
  `model` VARCHAR(50) NOT NULL COMMENT '操作模块',
  `action` VARCHAR(100) NOT NULL COMMENT '操作内容',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_account` (`account_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ===========================
-- 宠物社区模块
-- ===========================

-- 帖子表
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `user_id` BIGINT NOT NULL COMMENT '发帖用户ID',
  `title` VARCHAR(100) DEFAULT NULL COMMENT '帖子标题',
  `content` TEXT NOT NULL COMMENT '帖子内容',
  `images` TEXT DEFAULT NULL COMMENT '图片URL列表，逗号分隔',
  `video` VARCHAR(255) DEFAULT NULL COMMENT '视频URL',
  `post_type` TINYINT NOT NULL DEFAULT 0 COMMENT '帖子类型：0=纯文字 1=图文 2=视频',
  `category` VARCHAR(50) DEFAULT '其他' COMMENT '分类：日常分享、宠物医疗、养护知识、其他',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0=待审核 1=已发布 2=已拒绝 3=已删除',
  `like_count` INT DEFAULT 0 COMMENT '点赞数',
  `comment_count` INT DEFAULT 0 COMMENT '评论数',
  `view_count` INT DEFAULT 0 COMMENT '浏览数',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子表';

-- 帖子点赞表
DROP TABLE IF EXISTS `post_like`;
CREATE TABLE `post_like` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `post_id` BIGINT NOT NULL COMMENT '帖子ID',
  `user_id` BIGINT NOT NULL COMMENT '点赞用户ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_user` (`post_id`, `user_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_post_id` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子点赞表';

-- 帖子评论表
DROP TABLE IF EXISTS `post_comment`;
CREATE TABLE `post_comment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `post_id` BIGINT NOT NULL COMMENT '帖子ID',
  `user_id` BIGINT NOT NULL COMMENT '评论用户ID',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父评论ID，用于回复功能',
  `content` VARCHAR(500) NOT NULL COMMENT '评论内容',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子评论表';

-- ===========================
-- 宠物领养模块
-- ===========================

-- 宠物领养信息表
DROP TABLE IF EXISTS `adoption`;
CREATE TABLE `adoption` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '领养信息ID',
  `user_id` BIGINT NOT NULL COMMENT '发布用户ID',
  `pet_name` VARCHAR(50) NOT NULL COMMENT '宠物名称',
  `pet_type` VARCHAR(50) NOT NULL COMMENT '宠物类型：猫、狗、其他',
  `pet_breed` VARCHAR(50) DEFAULT NULL COMMENT '宠物品种',
  `pet_age` VARCHAR(20) DEFAULT NULL COMMENT '宠物年龄',
  `pet_gender` VARCHAR(10) DEFAULT NULL COMMENT '宠物性别：公、母、未知',
  `description` TEXT DEFAULT NULL COMMENT '描述信息',
  `images` TEXT DEFAULT NULL COMMENT '宠物图片URL列表，逗号分隔',
  `contact` VARCHAR(100) DEFAULT NULL COMMENT '联系方式',
  `location` VARCHAR(100) DEFAULT NULL COMMENT '所在地区',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0=待审核 1=已发布 2=已拒绝 3=已领养 4=已下架',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_pet_type` (`pet_type`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物领养信息表';

-- 领养申请表
DROP TABLE IF EXISTS `adoption_application`;
CREATE TABLE `adoption_application` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `adoption_id` BIGINT NOT NULL COMMENT '领养信息ID',
  `applicant_id` BIGINT NOT NULL COMMENT '申请人用户ID',
  `message` TEXT DEFAULT NULL COMMENT '申请留言',
  `contact` VARCHAR(100) DEFAULT NULL COMMENT '申请人联系方式',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0=待审核 1=已通过 2=已拒绝',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_adoption_id` (`adoption_id`),
  KEY `idx_applicant_id` (`applicant_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='领养申请表';

-- ===========================
-- 宠物档案与健康模块
-- ===========================

-- 宠物档案表
DROP TABLE IF EXISTS `pet_profile`;
CREATE TABLE `pet_profile` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '宠物档案ID',
  `user_id` BIGINT NOT NULL COMMENT '宠物主人用户ID',
  `pet_name` VARCHAR(50) NOT NULL COMMENT '宠物名称',
  `pet_type` VARCHAR(50) NOT NULL COMMENT '宠物类型：猫、狗、其他',
  `breed` VARCHAR(50) DEFAULT NULL COMMENT '品种',
  `gender` VARCHAR(10) DEFAULT NULL COMMENT '性别：公、母',
  `birthday` DATE DEFAULT NULL COMMENT '出生日期',
  `color` VARCHAR(50) DEFAULT NULL COMMENT '毛色',
  `weight` DECIMAL(5, 2) DEFAULT NULL COMMENT '体重(kg)',
  `description` TEXT DEFAULT NULL COMMENT '备注说明',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '宠物头像',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_pet_type` (`pet_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物档案表';

-- 宠物健康记录表
DROP TABLE IF EXISTS `health_record`;
CREATE TABLE `health_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '健康记录ID',
  `pet_id` BIGINT NOT NULL COMMENT '宠物档案ID',
  `record_type` VARCHAR(20) NOT NULL COMMENT '记录类型：疫苗、驱虫、体检、诊疗、其他',
  `title` VARCHAR(100) NOT NULL COMMENT '记录标题',
  `content` TEXT DEFAULT NULL COMMENT '详细内容',
  `record_date` DATE DEFAULT NULL COMMENT '记录日期',
  `doctor` VARCHAR(50) DEFAULT NULL COMMENT '医生姓名',
  `hospital` VARCHAR(100) DEFAULT NULL COMMENT '医院名称',
  `next_date` DATE DEFAULT NULL COMMENT '下次提醒日期',
  `attachments` TEXT DEFAULT NULL COMMENT '附件URL列表，逗号分隔',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_pet_id` (`pet_id`),
  KEY `idx_record_type` (`record_type`),
  KEY `idx_record_date` (`record_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物健康记录表';

-- ===========================
-- 服务模块
-- ===========================

-- 服务信息表
DROP TABLE IF EXISTS `service_info`;
CREATE TABLE `service_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '服务信息ID',
  `user_id` BIGINT NOT NULL COMMENT '发布者用户ID',
  `service_type` VARCHAR(50) NOT NULL COMMENT '服务类型：医疗、美容、寄养、训练、其他',
  `publish_type` TINYINT NOT NULL DEFAULT 0 COMMENT '发布类型：0=用户需求 1=服务提供',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `content` TEXT DEFAULT NULL COMMENT '详细描述',
  `images` TEXT DEFAULT NULL COMMENT '图片URL列表，逗号分隔',
  `pet_type` VARCHAR(50) DEFAULT NULL COMMENT '适用宠物类型',
  `location` VARCHAR(200) DEFAULT NULL COMMENT '地点/地址',
  `contact` VARCHAR(100) DEFAULT NULL COMMENT '联系方式',
  `price` DECIMAL(10, 2) DEFAULT NULL COMMENT '价格',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0=待审核 1=已发布 2=已拒绝 3=已下架 4=已完成',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `application_count` INT DEFAULT 0 COMMENT '申请数量',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_service_type` (`service_type`),
  KEY `idx_publish_type` (`publish_type`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务信息表';

-- 服务申请表
DROP TABLE IF EXISTS `service_application`;
CREATE TABLE `service_application` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `service_id` BIGINT NOT NULL COMMENT '服务信息ID',
  `user_id` BIGINT NOT NULL COMMENT '申请者用户ID',
  `pet_id` BIGINT DEFAULT NULL COMMENT '宠物ID（如果涉及具体宠物）',
  `message` TEXT DEFAULT NULL COMMENT '申请留言',
  `contact` VARCHAR(100) DEFAULT NULL COMMENT '联系方式',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0=待审核 1=已通过 2=已拒绝 3=已取消',
  `reply` TEXT DEFAULT NULL COMMENT '回复内容',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_service_id` (`service_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_pet_id` (`pet_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务申请表';

-- 服务预约表
DROP TABLE IF EXISTS `service_appointment`;
CREATE TABLE `service_appointment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `application_id` BIGINT DEFAULT NULL COMMENT '申请ID（如果有对应申请）',
  `service_id` BIGINT DEFAULT NULL COMMENT '服务信息ID（如果有对应服务）',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `pet_id` BIGINT DEFAULT NULL COMMENT '宠物档案ID',
  `service_type` VARCHAR(50) NOT NULL COMMENT '服务类型：美容、医疗、寄养、训练、其他',
  `service_name` VARCHAR(100) NOT NULL COMMENT '服务名称',
  `appointment_time` DATETIME DEFAULT NULL COMMENT '预约时间',
  `location` VARCHAR(200) DEFAULT NULL COMMENT '服务地点',
  `contact` VARCHAR(100) DEFAULT NULL COMMENT '联系方式',
  `message` TEXT DEFAULT NULL COMMENT '备注信息',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0=待确认 1=已确认 2=已完成 3=已取消',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_application_id` (`application_id`),
  KEY `idx_service_id` (`service_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_pet_id` (`pet_id`),
  KEY `idx_appointment_time` (`appointment_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务预约表';

-- ===========================
-- 活动模块
-- ===========================

-- 活动表
DROP TABLE IF EXISTS `activity`;
CREATE TABLE `activity` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `title` VARCHAR(200) NOT NULL COMMENT '活动标题',
  `content` TEXT NOT NULL COMMENT '活动内容详情',
  `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
  `images` TEXT DEFAULT NULL COMMENT '活动图片列表，逗号分隔',
  `activity_type` VARCHAR(50) NOT NULL COMMENT '活动类型：线上/线下/混合',
  `location` VARCHAR(200) DEFAULT NULL COMMENT '活动地点',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `end_time` DATETIME NOT NULL COMMENT '结束时间',
  `registration_deadline` DATETIME DEFAULT NULL COMMENT '报名截止时间',
  `max_participants` INT DEFAULT 0 COMMENT '最大参与人数，0表示无限制',
  `current_participants` INT DEFAULT 0 COMMENT '当前报名人数',
  `organizer` VARCHAR(100) DEFAULT NULL COMMENT '主办方',
  `contact` VARCHAR(100) DEFAULT NULL COMMENT '联系方式',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0=待审核 1=已发布 2=已拒绝 3=已取消 4=进行中 5=已结束',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `create_by` BIGINT NOT NULL COMMENT '创建者ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_create_by` (`create_by`),
  KEY `idx_status` (`status`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_activity_type` (`activity_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动表';

-- 活动报名表
DROP TABLE IF EXISTS `activity_registration`;
CREATE TABLE `activity_registration` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '报名ID',
  `activity_id` BIGINT NOT NULL COMMENT '活动ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `message` TEXT DEFAULT NULL COMMENT '报名留言',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0=已取消 1=已报名 2=已签到',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_user` (`activity_id`, `user_id`),
  KEY `idx_activity_id` (`activity_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动报名表';

-- ===========================
-- 资讯模块
-- ===========================

-- 资讯信息表
DROP TABLE IF EXISTS `news`;
CREATE TABLE `news` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '资讯ID',
  `title` VARCHAR(200) NOT NULL COMMENT '资讯标题',
  `summary` VARCHAR(500) DEFAULT NULL COMMENT '摘要',
  `content` TEXT NOT NULL COMMENT '资讯内容',
  `cover_image` VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
  `category` VARCHAR(50) NOT NULL COMMENT '分类：养护知识、医疗健康、训练技巧、行业动态、其他',
  `tags` VARCHAR(200) DEFAULT NULL COMMENT '标签，逗号分隔',
  `source` VARCHAR(100) DEFAULT NULL COMMENT '来源',
  `author` VARCHAR(50) DEFAULT NULL COMMENT '作者',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0=草稿 1=已发布 2=已下架',
  `is_top` TINYINT DEFAULT 0 COMMENT '是否置顶：0=否 1=是',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `like_count` INT DEFAULT 0 COMMENT '点赞次数',
  `create_by` BIGINT NOT NULL COMMENT '创建者ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `publish_time` DATETIME DEFAULT NULL COMMENT '发布时间',
  PRIMARY KEY (`id`),
  KEY `idx_create_by` (`create_by`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`),
  KEY `idx_is_top` (`is_top`),
  KEY `idx_publish_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资讯信息表';

SET FOREIGN_KEY_CHECKS = 1;

-- ===========================
-- 数据字典说明
-- ===========================

/*
========================
用户账户模块
========================

account.user_type:
  0 - 普通用户
  1 - 管理员

account.status:
  0 - 禁用
  1 - 正常

notice.notice_type:
  0 - 系统通知
  1 - 点赞
  2 - 评论
  3 - 关注
  4 - 其他

notice.is_read:
  0 - 未读
  1 - 已读

========================
宠物社区模块
========================

post.post_type:
  0 - 纯文字帖子
  1 - 图文帖子
  2 - 视频帖子

post.category:
  日常分享、宠物医疗、养护知识、其他

post.status:
  0 - 待审核
  1 - 已发布
  2 - 已拒绝
  3 - 已删除

========================
宠物领养模块
========================

adoption.pet_type:
  猫、狗、兔子、仓鼠、其他

adoption.pet_gender:
  公、母、未知

adoption.status:
  0 - 待审核
  1 - 已发布
  2 - 已拒绝
  3 - 已领养
  4 - 已下架

adoption_application.status:
  0 - 待审核
  1 - 已通过
  2 - 已拒绝

========================
宠物档案与健康模块
========================

pet_profile.pet_type:
  猫、狗、其他

pet_profile.gender:
  公、母

health_record.record_type:
  疫苗、驱虫、体检、诊疗、其他

========================
服务模块
========================

service_info.service_type:
  医疗、美容、寄养、训练、其他

service_info.publish_type:
  0 - 用户需求
  1 - 服务提供

service_info.status:
  0 - 待审核
  1 - 已发布
  2 - 已拒绝
  3 - 已下架
  4 - 已完成

service_application.status:
  0 - 待审核
  1 - 已通过
  2 - 已拒绝
  3 - 已取消

service_appointment.status:
  0 - 待确认
  1 - 已确认
  2 - 已完成
  3 - 已取消

========================
活动模块
========================

activity.activity_type:
  线上、线下、混合

activity.status:
  0 - 待审核
  1 - 已发布
  2 - 已拒绝
  3 - 已取消
  4 - 进行中
  5 - 已结束

activity_registration.status:
  0 - 已取消
  1 - 已报名
  2 - 已签到

========================
资讯模块
========================

news.category:
  养护知识、医疗健康、训练技巧、行业动态、其他

news.status:
  0 - 草稿
  1 - 已发布
  2 - 已下架

news.is_top:
  0 - 否
  1 - 是
*/
