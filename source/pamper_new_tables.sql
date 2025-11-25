/*
 Navicat Premium Data Transfer

 Source Server         : LocalMysql
 Source Server Type    : MySQL
 Source Server Version : 80040
 Source Host           : localhost:3306
 Source Schema         : pamper

 Target Server Type    : MySQL
 Target Server Version : 80040
 File Encoding         : 65001

 Date: 25/11/2025 - 新增表结构
 说明：此文件包含宠物交流与服务平台的新增表结构
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ===========================
-- 宠物交流社区模块
-- ===========================

-- ----------------------------
-- Table structure for post
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '发帖用户ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '帖子标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '帖子内容',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片URL列表，用逗号分隔',
  `video` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '视频URL',
  `post_type` int NOT NULL DEFAULT 0 COMMENT '帖子类型：0纯文字，1图文，2视频',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '其他' COMMENT '分类：日常分享、宠物医疗、养护知识、其他',
  `status` int NOT NULL DEFAULT 0 COMMENT '状态：0待审核，1已发布，2已拒绝，3已删除',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` int NULL DEFAULT 0 COMMENT '评论数',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览数',
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_category`(`category`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '帖子表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for post_like
-- ----------------------------
DROP TABLE IF EXISTS `post_like`;
CREATE TABLE `post_like`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `post_id` int NOT NULL COMMENT '帖子ID',
  `user_id` int NOT NULL COMMENT '点赞用户ID',
  `create_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_post_user`(`post_id`, `user_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '帖子点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for post_comment
-- ----------------------------
DROP TABLE IF EXISTS `post_comment`;
CREATE TABLE `post_comment`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `post_id` int NOT NULL COMMENT '帖子ID',
  `user_id` int NOT NULL COMMENT '评论用户ID',
  `parent_id` int NULL DEFAULT NULL COMMENT '父评论ID，用于回复功能',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '评论内容',
  `create_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_post_id`(`post_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '帖子评论表' ROW_FORMAT = Dynamic;

-- ===========================
-- 宠物领养模块
-- ===========================

-- ----------------------------
-- Table structure for adoption
-- ----------------------------
DROP TABLE IF EXISTS `adoption`;
CREATE TABLE `adoption`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '发布用户ID',
  `pet_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '宠物名称',
  `pet_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '宠物类型：猫、狗、其他',
  `pet_breed` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '宠物品种',
  `pet_age` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '宠物年龄',
  `pet_gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '宠物性别：公、母、未知',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述信息',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '宠物图片URL列表，用逗号分隔',
  `contact` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系方式',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '所在地区',
  `status` int NOT NULL DEFAULT 0 COMMENT '状态：0待审核，1已发布，2已拒绝，3已领养，4已下架',
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_pet_type`(`pet_type`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '宠物领养信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for adoption_application
-- ----------------------------
DROP TABLE IF EXISTS `adoption_application`;
CREATE TABLE `adoption_application`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `adoption_id` int NOT NULL COMMENT '领养信息ID',
  `applicant_id` int NOT NULL COMMENT '申请人用户ID',
  `message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '申请留言',
  `contact` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '申请人联系方式',
  `status` int NOT NULL DEFAULT 0 COMMENT '状态：0待审核，1已通过，2已拒绝',
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_adoption_id`(`adoption_id`) USING BTREE,
  INDEX `idx_applicant_id`(`applicant_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '领养申请表' ROW_FORMAT = Dynamic;

-- ===========================
-- 宠物医疗与服务模块
-- ===========================

-- ----------------------------
-- Table structure for pet_profile
-- ----------------------------
DROP TABLE IF EXISTS `pet_profile`;
CREATE TABLE `pet_profile`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '宠物主人用户ID',
  `pet_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '宠物名称',
  `pet_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '宠物类型：猫、狗、其他',
  `breed` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '品种',
  `gender` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '性别：公、母',
  `birthday` date NULL DEFAULT NULL COMMENT '出生日期',
  `color` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '毛色',
  `weight` decimal(5, 2) NULL DEFAULT NULL COMMENT '体重(kg)',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注说明',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '宠物头像',
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '宠物档案表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for health_record
-- ----------------------------
DROP TABLE IF EXISTS `health_record`;
CREATE TABLE `health_record`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `pet_id` int NOT NULL COMMENT '宠物档案ID',
  `record_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '记录类型：疫苗、驱虫、体检、诊疗、其他',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '记录标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '详细内容',
  `record_date` date NULL DEFAULT NULL COMMENT '记录日期',
  `doctor` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '医生姓名',
  `hospital` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '医院名称',
  `next_date` date NULL DEFAULT NULL COMMENT '下次提醒日期',
  `attachments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '附件URL列表，用逗号分隔',
  `create_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_pet_id`(`pet_id`) USING BTREE,
  INDEX `idx_record_type`(`record_type`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '宠物健康记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for service_appointment
-- ----------------------------
DROP TABLE IF EXISTS `service_appointment`;
CREATE TABLE `service_appointment`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '预约用户ID',
  `pet_id` int NULL DEFAULT NULL COMMENT '宠物档案ID',
  `service_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '服务类型：美容、医疗、寄养、训练、其他',
  `service_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '服务名称',
  `appointment_time` datetime NULL DEFAULT NULL COMMENT '预约时间',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务地点',
  `contact` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系方式',
  `message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  `status` int NOT NULL DEFAULT 0 COMMENT '状态：0待确认，1已确认，2已完成，3已取消',
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_pet_id`(`pet_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '服务预约表' ROW_FORMAT = Dynamic;

-- ===========================
-- 活动与咨询模块
-- ===========================

-- ----------------------------
-- Table structure for activity
-- ----------------------------
DROP TABLE IF EXISTS `activity`;
CREATE TABLE `activity`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '活动标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '活动描述',
  `cover_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '封面图片',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '活动地点',
  `max_participants` int NULL DEFAULT NULL COMMENT '最大参与人数',
  `current_participants` int NULL DEFAULT 0 COMMENT '当前参与人数',
  `status` int NOT NULL DEFAULT 0 COMMENT '状态：0未开始，1进行中，2已结束，3已取消',
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_start_time`(`start_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '活动表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for activity_registration
-- ----------------------------
DROP TABLE IF EXISTS `activity_registration`;
CREATE TABLE `activity_registration`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `activity_id` int NOT NULL COMMENT '活动ID',
  `user_id` int NOT NULL COMMENT '报名用户ID',
  `message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '报名留言',
  `status` int NOT NULL DEFAULT 0 COMMENT '状态：0待审核，1已通过，2已拒绝',
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_activity_user`(`activity_id`, `user_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '活动报名表' ROW_FORMAT = Dynamic;

-- ===========================
-- 后台管理与统计模块
-- ===========================

-- ----------------------------
-- Table structure for platform_statistics
-- ----------------------------
DROP TABLE IF EXISTS `platform_statistics`;
CREATE TABLE `platform_statistics`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `stat_date` date NOT NULL COMMENT '统计日期',
  `daily_active_users` int NULL DEFAULT 0 COMMENT '日活跃用户数',
  `new_users` int NULL DEFAULT 0 COMMENT '新增用户数',
  `new_posts` int NULL DEFAULT 0 COMMENT '新增帖子数',
  `new_comments` int NULL DEFAULT 0 COMMENT '新增评论数',
  `new_likes` int NULL DEFAULT 0 COMMENT '新增点赞数',
  `new_adoptions` int NULL DEFAULT 0 COMMENT '新增领养信息数',
  `new_activities` int NULL DEFAULT 0 COMMENT '新增活动数',
  `create_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_stat_date`(`stat_date`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '平台统计表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

-- ===========================
-- 数据字典说明
-- ===========================

/*
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

adoption.pet_type:
  猫、狗、兔子、仓鼠、其他

adoption.status:
  0 - 待审核
  1 - 已发布
  2 - 已拒绝
  3 - 已领养
  4 - 已下架

health_record.record_type:
  疫苗、驱虫、体检、诊疗、其他

service_appointment.service_type:
  美容、医疗、寄养、训练、其他

service_appointment.status:
  0 - 待确认
  1 - 已确认
  2 - 已完成
  3 - 已取消

activity.status:
  0 - 未开始
  1 - 进行中
  2 - 已结束
  3 - 已取消

activity_registration.status:
  0 - 待审核
  1 - 已通过
  2 - 已拒绝
*/
