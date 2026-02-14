-- =====================================================
-- Cloud Govern 数据库初始化脚本
-- 创建日期: 2026-02-13
-- 说明: 创建业务数据库和基础表结构
-- =====================================================

-- 设置字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- ---------------------------------------------------
-- 1. 创建 Nacos 数据库（Nacos 自行创建，这里预留）
-- ---------------------------------------------------
CREATE DATABASE IF NOT EXISTS `nacos` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ---------------------------------------------------
-- 2. 创建 Cloud Govern 业务数据库
-- ---------------------------------------------------
CREATE DATABASE IF NOT EXISTS `cloud_govern` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `cloud_govern`;

-- ---------------------------------------------------
-- 3. 用户表 (sys_user)
-- ---------------------------------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `status` tinyint DEFAULT 1 COMMENT '状态：0禁用 1启用',
  `deleted` tinyint DEFAULT 0 COMMENT '删除标志：0未删除 1已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ---------------------------------------------------
-- 4. 角色表 (sys_role)
-- ---------------------------------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称',
  `role_key` varchar(50) NOT NULL COMMENT '角色权限字符',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `status` tinyint DEFAULT 1 COMMENT '状态：0禁用 1启用',
  `deleted` tinyint DEFAULT 0 COMMENT '删除标志',
  `sort` int DEFAULT 0 COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_key` (`role_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- ---------------------------------------------------
-- 5. 菜单表 (sys_menu)
-- ---------------------------------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `parent_id` bigint DEFAULT 0 COMMENT '父菜单ID',
  `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
  `menu_type` tinyint NOT NULL COMMENT '类型：1目录 2菜单 3按钮',
  `path` varchar(200) DEFAULT NULL COMMENT '路由路径',
  `component` varchar(200) DEFAULT NULL COMMENT '组件路径',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT NULL COMMENT '图标',
  `sort` int DEFAULT 0 COMMENT '排序',
  `visible` tinyint DEFAULT 1 COMMENT '是否可见',
  `status` tinyint DEFAULT 1 COMMENT '状态',
  `deleted` tinyint DEFAULT 0,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单权限表';

-- ---------------------------------------------------
-- 6. 用户角色关联表 (sys_user_role)
-- ---------------------------------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- ---------------------------------------------------
-- 7. 角色菜单关联表 (sys_role_menu)
-- ---------------------------------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';

-- ---------------------------------------------------
-- 8. 审计日志表 (sys_audit_log)
-- ---------------------------------------------------
DROP TABLE IF EXISTS `sys_audit_log`;
CREATE TABLE `sys_audit_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL COMMENT '操作用户ID',
  `username` varchar(50) DEFAULT NULL COMMENT '操作用户名',
  `operation` varchar(100) DEFAULT NULL COMMENT '操作描述',
  `method` varchar(200) DEFAULT NULL COMMENT '请求方法',
  `params` text COMMENT '请求参数',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `result` text COMMENT '操作结果',
  `status` tinyint DEFAULT 1 COMMENT '操作状态：1成功 0失败',
  `error_msg` text COMMENT '错误信息',
  `duration` bigint DEFAULT NULL COMMENT '执行时长(ms)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审计日志表';

-- ---------------------------------------------------
-- 9. 网关路由表 (gateway_route)
-- ---------------------------------------------------
DROP TABLE IF EXISTS `gateway_route`;
CREATE TABLE `gateway_route` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `route_id` varchar(50) NOT NULL COMMENT '路由ID',
  `route_name` varchar(100) DEFAULT NULL COMMENT '路由名称',
  `uri` varchar(200) NOT NULL COMMENT '目标URI',
  `predicates` text COMMENT '断言配置(JSON)',
  `filters` text COMMENT '过滤器配置(JSON)',
  `metadata` text COMMENT '元数据(JSON)',
  `order` int DEFAULT 0 COMMENT '排序',
  `status` tinyint DEFAULT 1 COMMENT '状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_route_id` (`route_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='网关路由表';

-- ---------------------------------------------------
-- 10. 告警规则表 (alert_rule)
-- ---------------------------------------------------
DROP TABLE IF EXISTS `alert_rule`;
CREATE TABLE `alert_rule` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '规则名称',
  `service_name` varchar(100) DEFAULT NULL COMMENT '服务名称',
  `metric_type` varchar(50) NOT NULL COMMENT '指标类型',
  `condition` varchar(20) NOT NULL COMMENT '条件：gt/lt/eq',
  `threshold` decimal(10,2) NOT NULL COMMENT '阈值',
  `duration` int DEFAULT 60 COMMENT '持续时间(秒)',
  `notify_channels` varchar(255) DEFAULT NULL COMMENT '通知渠道(JSON)',
  `status` tinyint DEFAULT 1,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='告警规则表';

-- ---------------------------------------------------
-- 11. 初始化数据
-- ---------------------------------------------------

-- 初始化管理员用户 (密码: admin123, 使用 BCrypt 加密)
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `email`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKTNDp8G3vQMWR.XHGJ.Z5QX.ZQq', '系统管理员', 'admin@example.com', 1);

-- 初始化角色
INSERT INTO `sys_role` (`role_name`, `role_key`, `description`, `sort`) VALUES
('超级管理员', 'admin', '拥有所有权限', 1),
('运维人员', 'operator', '负责服务运维', 2),
('开发人员', 'developer', '负责开发测试', 3);

-- 初始化菜单
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `perms`, `icon`, `sort`) VALUES
(1, 0, '系统管理', 1, '/system', NULL, NULL, 'setting', 1),
(2, 1, '用户管理', 2, '/system/user', 'system/user/index', 'system:user:list', 'user', 1),
(3, 1, '角色管理', 2, '/system/role', 'system/role/index', 'system:role:list', 'usergroup', 2),
(4, 1, '菜单管理', 2, '/system/menu', 'system/menu/index', 'system:menu:list', 'menu', 3),
(5, 0, '服务管理', 1, '/service', NULL, NULL, 'cloud-server', 2),
(6, 5, '服务列表', 2, '/service/list', 'service/list/index', 'service:list:list', 'unordered-list', 1),
(7, 5, '服务详情', 2, '/service/detail', 'service/detail/index', 'service:detail:list', 'file-search', 2),
(8, 0, '配置中心', 1, '/config', NULL, NULL, 'file-text', 3),
(9, 8, '命名空间', 2, '/config/namespace', 'config/namespace/index', 'config:namespace:list', 'folder', 1),
(10, 8, '配置列表', 2, '/config/list', 'config/list/index', 'config:list:list', 'file', 2),
(11, 0, '服务监控', 1, '/monitor', NULL, NULL, 'dashboard', 4),
(12, 11, '监控面板', 2, '/monitor/dashboard', 'monitor/dashboard/index', 'monitor:dashboard:list', 'dashboard', 1),
(13, 11, '告警规则', 2, '/monitor/alert', 'monitor/alert/index', 'monitor:alert:list', 'error-circle', 2),
(14, 0, '网关管理', 1, '/gateway', NULL, NULL, 'api', 5),
(15, 14, '路由规则', 2, '/gateway/route', 'gateway/route/index', 'gateway:route:list', 'route', 1),
(16, 14, '限流规则', 2, '/gateway/rule', 'gateway/rule/index', 'gateway:rule:list', 'filter', 2);

-- 按钮权限
INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_type`, `perms`, `sort`) VALUES
(2, '用户查询', 3, 'system:user:query', 1),
(2, '用户新增', 3, 'system:user:add', 2),
(2, '用户修改', 3, 'system:user:edit', 3),
(2, '用户删除', 3, 'system:user:delete', 4),
(3, '角色查询', 3, 'system:role:query', 1),
(3, '角色新增', 3, 'system:role:add', 2),
(3, '角色修改', 3, 'system:role:edit', 3),
(3, '角色删除', 3, 'system:role:delete', 4);

-- 分配管理员角色
INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);

-- 分配角色菜单权限（超级管理员拥有所有菜单）
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, id FROM `sys_menu`;

-- ---------------------------------------------------
-- 完成
-- ---------------------------------------------------
SELECT 'Cloud Govern 数据库初始化完成!' AS message;
