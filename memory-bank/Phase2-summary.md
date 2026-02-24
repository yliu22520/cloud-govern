# Phase 2 完成总结 - 用户管理模块

> 完成日期: 2026-02-23

---

## 概述

Phase 2 实现了用户、角色、菜单的完整管理功能，包括后端 API 和前端页面。

---

## 已完成功能

### 2.1 系统服务 (cloud-govern-system)

| 组件 | 状态 | 说明 |
|------|------|------|
| 启动类 | ✅ | Spring Boot 应用启动 |
| 数据库配置 | ✅ | MySQL + MyBatis-Plus |
| Nacos 注册 | ✅ | 服务自动注册 |
| 实体类 | ✅ | SysUser, SysRole, SysMenu, SysUserRole, SysRoleMenu |
| Mapper | ✅ | 继承 BaseMapper + 自定义查询 |
| Service | ✅ | 用户、角色、菜单业务逻辑 |
| Controller | ✅ | REST API 接口 |

### 2.2 用户管理 API

| API | 方法 | 路径 | 说明 |
|-----|------|------|------|
| 用户列表 | GET | /system/user/list | 分页查询 |
| 用户详情 | GET | /system/user/{id} | 单个查询 |
| 创建用户 | POST | /system/user | 新增用户 |
| 更新用户 | PUT | /system/user/{id} | 修改用户 |
| 删除用户 | DELETE | /system/user/{id} | 逻辑删除 |
| 重置密码 | PUT | /system/user/{id}/password | 密码重置 |
| 更新状态 | PUT | /system/user/{id}/status | 启用/禁用 |

### 2.3 角色管理 API

| API | 方法 | 路径 | 说明 |
|-----|------|------|------|
| 角色列表 | GET | /system/role/list | 分页查询 |
| 所有角色 | GET | /system/role/all | 不分页 |
| 角色详情 | GET | /system/role/{id} | 单个查询 |
| 创建角色 | POST | /system/role | 新增角色 |
| 更新角色 | PUT | /system/role/{id} | 修改角色 |
| 删除角色 | DELETE | /system/role/{id} | 逻辑删除 |
| 更新状态 | PUT | /system/role/{id}/status | 启用/禁用 |

### 2.4 菜单管理 API

| API | 方法 | 路径 | 说明 |
|-----|------|------|------|
| 菜单树 | GET | /system/menu/tree | 树形结构 |
| 菜单详情 | GET | /system/menu/{id} | 单个查询 |
| 创建菜单 | POST | /system/menu | 新增菜单 |
| 更新菜单 | PUT | /system/menu/{id} | 修改菜单 |
| 删除菜单 | DELETE | /system/menu/{id} | 逻辑删除 |

### 2.5 前端页面

| 页面 | 路径 | 说明 |
|------|------|------|
| 用户管理 | /system/user | 用户列表、新增、编辑、删除、密码重置 |
| 角色管理 | /system/role | 角色列表、新增、编辑、删除、菜单权限配置 |
| 菜单管理 | /system/menu | 菜单树形表格、新增、编辑、删除 |

---

## 技术亮点

### 1. MyBatis-Plus 自动填充

```java
@Configuration
public class MybatisPlusConfig implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
```

### 2. 用户名唯一性校验

```java
@Select("SELECT * FROM sys_user WHERE username = #{username} LIMIT 1")
SysUser selectByUsernameIgnoreDelete(@Param("username") String username);
```

检查用户名时包含已逻辑删除的用户，避免数据库唯一键冲突。

### 3. 角色菜单关联验证

保存角色菜单关联时，验证菜单ID是否存在于数据库中：

```java
List<Long> existingMenuIds = sysMenuMapper.selectList(
    new LambdaQueryWrapper<SysMenu>()
        .in(SysMenu::getId, validMenuIds)
).stream().map(SysMenu::getId).collect(Collectors.toList());
```

### 4. Sa-Token 跨服务认证

通过 Redis 共享 Token 实现跨服务认证：

```xml
<dependency>
    <groupId>cn.dev33</groupId>
    <artifactId>sa-token-redis-jackson</artifactId>
</dependency>
```

---

## 问题修复记录

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| 用户列表返回0条 | deleted字段为NULL，逻辑删除过滤 | 新增自动填充配置 |
| 创建用户重复报错 | 只检查未删除用户，数据库唯一键冲突 | 新增 selectByUsernameIgnoreDelete 方法 |
| 角色修改报500 | menuIds包含不存在的菜单ID | 验证菜单存在性后再保存 |
| 错误消息不显示 | 前端读取message字段，后端返回msg | 兼容两种字段名 |
| 中文乱码 | MySQL客户端字符集问题 | 设置 SET NAMES utf8mb4 |

---

## 文件结构

```
cloud-govern-system/
├── src/main/java/com/cloud/govern/system/
│   ├── config/
│   │   └── MybatisPlusConfig.java        # 分页插件 + 自动填充
│   ├── controller/
│   │   ├── SysUserController.java
│   │   ├── SysRoleController.java
│   │   └── SysMenuController.java
│   ├── service/
│   │   ├── SysUserService.java
│   │   ├── SysRoleService.java
│   │   └── SysMenuService.java
│   ├── mapper/
│   │   ├── SysUserMapper.java
│   │   ├── SysRoleMapper.java
│   │   ├── SysMenuMapper.java
│   │   ├── SysUserRoleMapper.java
│   │   └── SysRoleMenuMapper.java
│   ├── entity/
│   │   ├── SysUser.java
│   │   ├── SysRole.java
│   │   ├── SysMenu.java
│   │   ├── SysUserRole.java
│   │   └── SysRoleMenu.java
│   └── dto/
│       ├── UserDTO.java, UserVO.java, UserQueryDTO.java
│       ├── RoleDTO.java, RoleVO.java, RoleQueryDTO.java
│       └── MenuDTO.java, MenuVO.java, MenuQueryDTO.java
```

---

## 脚本整理

将散落的脚本文件整理到 `scripts/` 目录：

```
scripts/
├── start/           # 启动脚本
├── build/           # 构建脚本
├── test/            # 测试脚本
└── tools/           # 工具脚本
```

---

## 下一步计划

Phase 3: 服务注册管理模块
- 封装 Nacos Open API 客户端
- 实现服务列表、实例管理功能
- 前端服务管理页面

---

**维护者**: AI Assistant
