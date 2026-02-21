# Phase 1 技术要点总结

> 完成日期: 2026-02-17

---

## 一、后端架构

### 1.1 模块结构

```
cloud-govern/
├── cloud-govern-common/     # 公共模块
├── cloud-govern-gateway/    # 网关服务 (端口 8080)
├── cloud-govern-auth/       # 认证服务 (端口 8100)
├── cloud-govern-system/     # 系统服务 (待开发)
├── cloud-govern-registry/   # 服务注册模块 (待开发)
├── cloud-govern-config/     # 配置中心模块 (待开发)
└── cloud-govern-monitor/    # 监控模块 (待开发)
```

### 1.2 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.2.x | 基础框架 |
| Spring Cloud | 2023.x | 微服务框架 |
| Nacos | 2.x | 服务注册/配置中心 |
| Sa-Token | 1.37.0 | 认证授权 |
| MyBatis-Plus | 3.5.x | ORM 框架 |
| Hutool | 5.8.x | 工具库 |

---

## 二、关键实现

### 2.1 Gateway 认证过滤器

**文件**: `cloud-govern-gateway/src/main/java/com/cloud/govern/gateway/config/SaTokenConfig.java`

```java
@Configuration
public class SaTokenConfig {
    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogic("login") {
            @Override
            public SaTokenConfig getConfig() {
                SaTokenConfig config = new SaTokenConfig();
                config.setTokenName("Authorization");
                config.setTokenStyle("uuid");
                config.setTimeout(86400);
                config.setIsShare(false);
                return config;
            }
        };
    }
}
```

**认证流程**:
1. 请求到达 Gateway
2. SaTokenConfig 检查请求路径是否在排除列表
3. 需认证的请求验证 Token
4. 通过则转发到下游服务

**排除路径** (无需认证):
- `/auth/login`
- `/auth/logout`
- `/auth/register`

### 2.2 登录接口实现

**文件**: `cloud-govern-auth/src/main/java/com/cloud/govern/auth/service/AuthService.java`

```java
public LoginVO login(LoginDTO dto) {
    // 1. 查询用户
    SysUser user = userMapper.selectByUsername(dto.getUsername());
    if (user == null) {
        throw new BusinessException("用户名或密码错误");
    }

    // 2. 验证密码 (Hutool BCrypt)
    if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
        throw new BusinessException("用户名或密码错误");
    }

    // 3. 检查状态
    if (user.getStatus() != 1) {
        throw new BusinessException("账号已被禁用");
    }

    // 4. 生成 Token
    StpUtil.login(user.getId());
    String token = StpUtil.getTokenValue();

    // 5. 返回结果
    return buildLoginVO(user, token);
}
```

### 2.3 统一响应格式

**文件**: `cloud-govern-common/src/main/java/com/cloud/govern/common/result/R.java`

```java
public class R<T> {
    private Integer code;
    private String message;
    private T data;
    private Long timestamp;

    public static <T> R<T> ok(T data) {
        return new R<>(200, "success", data);
    }

    public static <T> R<T> fail(Integer code, String message) {
        return new R<>(code, message, null);
    }
}
```

---

## 三、遇到的问题与解决方案

### 3.1 BCrypt 密码验证失败

**问题**: 初始 SQL 中的密码哈希与 Hutool BCrypt 验证不匹配

**原因**: 不同 BCrypt 实现可能生成不同的哈希格式

**解决**: 使用 Hutool BCrypt 重新生成哈希
```java
String hash = BCrypt.hashpw("admin123", BCrypt.gensalt());
// 结果: $2a$10$Bwrxq2hzqlmxRSMNNqz.ZuhZpgXCa/g.X9YKvp.e9vWeGFdMXBN.2
```

### 3.2 @RequestParam 参数名丢失

**问题**: 编译后参数名不可用，导致 `@RequestParam` 无法自动绑定

**原因**: JDK 8+ 默认不保留参数名

**解决**: 显式指定 name 属性
```java
// 错误
public R<?> resetPassword(@RequestParam String username)

// 正确
public R<?> resetPassword(@RequestParam(name = "username") String username)
```

### 3.3 bootstrap.yml 不加载

**问题**: Spring Boot 3.x 不再自动加载 bootstrap.yml

**原因**: Spring Cloud 2023.x 移除了 bootstrap 上下文

**解决**: 重命名为 application.yml

### 3.4 TDesign 组件自动导入失败

**问题**: unplugin-vue-components 自动导入解析到错误的包名 `tdesign-vue`

**原因**: TDesignResolver 默认配置不匹配

**解决**: 移除自动导入插件，改为手动导入
```typescript
import { Form as TForm, Input as TInput } from 'tdesign-vue-next';
```

---

## 四、前端架构

### 4.1 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.4.x | 前端框架 |
| Vite | 5.x | 构建工具 |
| TDesign | 1.9.x | UI 组件库 |
| Pinia | 2.x | 状态管理 |
| Vue Router | 4.x | 路由管理 |
| Axios | 1.6.x | HTTP 客户端 |

### 4.2 项目结构

```
cloud-govern-ui/
├── src/
│   ├── api/           # API 接口
│   │   └── auth.ts    # 认证相关接口
│   ├── layouts/       # 布局组件
│   │   └── default.vue
│   ├── pages/         # 页面
│   │   ├── login/     # 登录页
│   │   ├── dashboard/ # 首页
│   │   └── error/     # 错误页
│   ├── router/        # 路由配置
│   ├── store/         # 状态管理
│   │   ├── index.ts
│   │   └── user.ts    # 用户状态
│   ├── styles/        # 样式文件
│   └── utils/         # 工具函数
│       └── request.ts # Axios 封装
├── .eslintrc.cjs      # ESLint 配置
├── vite.config.ts     # Vite 配置
└── package.json
```

### 4.3 API 代理配置

**文件**: `vite.config.ts`

```typescript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',  // Gateway 地址
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, ''),
    },
  },
}
```

### 4.4 请求拦截器

**文件**: `src/utils/request.ts`

```typescript
// 请求拦截 - 自动添加 Token
request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 响应拦截 - 统一错误处理
request.interceptors.response.use(
  (response) => {
    if (response.data.code === 200) {
      return response.data.data;
    }
    MessagePlugin.error(response.data.message);
    return Promise.reject(new Error(response.data.message));
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
```

---

## 五、API 接口清单

### 5.1 认证模块

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/auth/login` | 用户登录 | 否 |
| POST | `/auth/logout` | 用户登出 | 是 |
| GET | `/auth/user/info` | 获取用户信息 | 是 |

### 5.2 登录请求示例

```bash
# 登录
curl -X POST "http://localhost:8080/auth/login?username=admin&password=admin123"

# 响应
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "xxx-xxx-xxx",
    "userId": 1,
    "username": "admin",
    "nickname": "系统管理员",
    "roles": ["admin"]
  },
  "timestamp": 1739750400000
}
```

---

## 六、部署说明

### 6.1 基础设施

```bash
# 启动 Nacos、MySQL、Redis
cd docker
docker-compose up -d
```

### 6.2 后端服务

```bash
# 开发模式 (推荐)
run-gateway.cmd  # 启动网关
run-auth.cmd     # 启动认证服务

# 生产模式 (需先打包)
mvn package -DskipTests
run-gateway.bat
run-auth.bat
```

### 6.3 前端服务

```bash
cd cloud-govern-ui
npm install
npm run dev  # 开发模式，访问 http://localhost:3000
```

---

## 七、后续计划

Phase 1 已完成，下一阶段开发内容：

| 阶段 | 内容 | 状态 |
|------|------|------|
| Phase 2 | 用户管理模块 | 待开始 |
| Phase 3 | 服务注册管理模块 | 待开始 |
| Phase 4 | 配置中心管理模块 | 待开始 |

---

**文档维护者**: AI Assistant
**最后更新**: 2026-02-17
