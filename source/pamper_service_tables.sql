-- 宠物医疗与服务模块数据库表

-- 服务信息表（包含用户需求发布和管理员服务发布）
CREATE TABLE `service_info` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '服务信息ID',
  `user_id` BIGINT NOT NULL COMMENT '发布者用户ID',
  `service_type` VARCHAR(50) NOT NULL COMMENT '服务类型：医疗、美容、寄养、训练、其他',
  `publish_type` TINYINT NOT NULL DEFAULT 0 COMMENT '发布类型：0=用户需求 1=服务提供',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `content` TEXT COMMENT '详细描述',
  `images` TEXT COMMENT '图片URL列表，逗号分隔',
  `pet_type` VARCHAR(50) COMMENT '适用宠物类型',
  `location` VARCHAR(200) COMMENT '地点/地址',
  `contact` VARCHAR(100) COMMENT '联系方式',
  `price` DECIMAL(10,2) COMMENT '价格',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0=待审核 1=已发布 2=已拒绝 3=已下架 4=已完成',
  `view_count` INT DEFAULT 0 COMMENT '浏览次数',
  `application_count` INT DEFAULT 0 COMMENT '申请数量',
  `create_time` VARCHAR(30) NOT NULL COMMENT '创建时间',
  `update_time` VARCHAR(30) NOT NULL COMMENT '更新时间',
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_service_type` (`service_type`),
  INDEX `idx_publish_type` (`publish_type`),
  INDEX `idx_status` (`status`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务信息表';

-- 服务申请表
CREATE TABLE `service_application` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '申请ID',
  `service_id` BIGINT NOT NULL COMMENT '服务信息ID',
  `user_id` BIGINT NOT NULL COMMENT '申请者用户ID',
  `pet_id` BIGINT COMMENT '宠物ID（如果涉及具体宠物）',
  `message` TEXT COMMENT '申请留言',
  `contact` VARCHAR(100) COMMENT '联系方式',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0=待审核 1=已通过 2=已拒绝 3=已取消',
  `reply` TEXT COMMENT '回复内容',
  `create_time` VARCHAR(30) NOT NULL COMMENT '申请时间',
  `update_time` VARCHAR(30) COMMENT '更新时间',
  INDEX `idx_service_id` (`service_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_status` (`status`),
  FOREIGN KEY (`service_id`) REFERENCES `service_info`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务申请表';

-- 预约记录表
CREATE TABLE `service_appointment` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '预约ID',
  `application_id` BIGINT NOT NULL COMMENT '申请ID',
  `service_id` BIGINT NOT NULL COMMENT '服务信息ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `appointment_time` VARCHAR(30) NOT NULL COMMENT '预约时间',
  `location` VARCHAR(200) COMMENT '预约地点',
  `notes` TEXT COMMENT '备注信息',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0=待确认 1=已确认 2=已完成 3=已取消',
  `create_time` VARCHAR(30) NOT NULL COMMENT '创建时间',
  `update_time` VARCHAR(30) COMMENT '更新时间',
  INDEX `idx_application_id` (`application_id`),
  INDEX `idx_service_id` (`service_id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_appointment_time` (`appointment_time`),
  INDEX `idx_status` (`status`),
  FOREIGN KEY (`application_id`) REFERENCES `service_application`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`service_id`) REFERENCES `service_info`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约记录表';
