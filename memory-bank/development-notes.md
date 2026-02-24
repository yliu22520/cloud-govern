# 开发经验与注意事项 - Cloud Govern

> 最后更新: 2026-02-23

---

## 环境配置

### JDK 版本
- **使用 JDK 21**，不要用 JDK 25（Lombok 不兼容）
- 路径: `D:\devlop\jdk-21.0.2`

### Maven 配置
- 使用 IDEA 内置 Maven: `D:\devlop\idea25\IntelliJ IDEA 2025.3.1\plugins\maven\lib\maven3\bin\mvn.cmd`
- 启动脚本需同时设置 `JAVA_HOME` 和 `PATH`

### 启动顺序
1. 先启动基础设施: MySQL (3306), Redis (6379), Nacos (8848)
2. 再启动后端服务: Gateway (8080) → Auth (8100) → System (8200)
3. 最后启动前端: cloud-govern-ui (3000)

---

## 数据库

### 字符集问题
- MySQL 客户端连接时必须设置字符集: `SET NAMES utf8mb4`
- 否则中文会显示为 `?????`
- 表已使用 `utf8mb4`，问题出在客户端连接

### MyBatis-Plus 自动填充
- `@TableField(fill = FieldFill.INSERT)` 只是声明，需要配置 `MetaObjectHandler`
- **必须配置** `MybatisPlusConfig implements MetaObjectHandler`
- 否则 `deleted`、`createTime`、`updateTime` 字段会是 NULL

```java
@Override
public void insertFill(MetaObject metaObject) {
    this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
    this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
    this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
}
```

### 逻辑删除问题
- 如果 `deleted` 字段为 NULL，MyBatis-Plus 会过滤掉（视为已删除）
- 修复已有数据: `UPDATE table SET deleted = 0 WHERE deleted IS NULL`

### 唯一性校验
- 数据库唯一键约束不会忽略逻辑删除的记录
- 检查重复时需要自定义 SQL 忽略逻辑删除标记

```java
@Select("SELECT * FROM sys_user WHERE username = #{username} LIMIT 1")
SysUser selectByUsernameIgnoreDelete(@Param("username") String username);
```

---

## Sa-Token 认证

### 跨服务 Token 问题
- Gateway 验证 Token，但 Auth 服务签发的 Token 默认只在 Auth 服务有效
- **必须配置 Redis 存储**，所有服务共享 Token

```xml
<dependency>
    <groupId>cn.dev33</groupId>
    <artifactId>sa-token-redis-jackson</artifactId>
</dependency>
```

### 错误消息字段
- Sa-Token 默认返回 `msg` 字段
- 前端可能期望 `message` 字段
- 需要兼容处理：

```typescript
const message = res.data?.message || res.data?.msg || '操作失败'
```

---

## 前端开发

### TypeScript 类型
- TDesign 组件的 `trigger` 属性需要 `as const`
- DropdownItem 类型要匹配 `value` 和 `content` 字段

### API 响应处理
- 统一在 `request.ts` 中处理响应和错误
- 注意 HTTP 状态码和业务状态码的区别

---

## 业务逻辑

### 关联数据验证
- 保存关联关系时，验证关联ID是否存在

```java
// 只保存数据库中存在的菜单
List<Long> existingMenuIds = sysMenuMapper.selectList(
    new LambdaQueryWrapper<SysMenu>()
        .in(SysMenu::getId, validMenuIds)
).stream().map(SysMenu::getId).collect(Collectors.toList());
```

### 分页查询
- MyBatis-Plus 分页需要配置 `PaginationInnerInterceptor`

```java
@Bean
public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
    return interceptor;
}
```

---

## Git 提交

### Windows 中文问题
- Windows CMD 中文 commit message 会乱码
- 使用英文 commit message 或通过 PowerShell 提交

---

## 脚本文件

### 目录结构
```
scripts/
├── start/           # 启动脚本
├── build/           # 构建脚本
├── test/            # 测试脚本
└── tools/           # 工具脚本
```

### 启动脚本要点
- 必须设置 `JAVA_HOME`
- 建议同时设置 `PATH=%JAVA_HOME%\bin;%PATH%`
- `start` 命令新窗口需要单独设置环境变量

---

## 常见错误速查

| 错误现象 | 可能原因 | 解决方案 |
|----------|----------|----------|
| 用户列表返回0条 | deleted 字段 NULL | 配置自动填充 |
| 创建用户重复报错 | 唯一键冲突 | 用自定义 SQL 检查所有记录 |
| Token 无效 | 跨服务未共享 | 配置 Redis 存储 |
| 中文显示 ???? | 字符集问题 | SET NAMES utf8mb4 |
| 分页无效 | 未配置插件 | 添加 PaginationInnerInterceptor |
| 角色修改报500 | 关联ID不存在 | 验证后再保存 |

---

**维护者**: AI Assistant
