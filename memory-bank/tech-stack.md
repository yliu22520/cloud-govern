# 技术选型文档 - Cloud Govern

> 版本: 1.0 | 创建日期: 2026-02-13

---

## 一、技术选型原则

### 1. 核心原则
- **熟悉优先**：优先使用团队熟悉的技术栈
- **生态完善**：选择社区活跃、文档完善的技术
- **简单优先**：MVP 阶段避免引入过度复杂的技术
- **可扩展性**：为后续迭代预留扩展空间

### 2. 版本策略
- 优先选择 LTS（长期支持）版本
- 核心依赖锁定主版本号
- 安全补丁及时更新

---

## 二、前端技术栈

### 2.1 核心框架

| 技术 | 版本 | 选型理由 |
|------|------|----------|
| **Vue 3** | 3.4+ | Composition API 更灵活，TypeScript 支持更好，性能提升显著 |
| **TypeScript** | 5.0+ | 类型安全，IDE 支持友好，大型项目必备 |
| **Vite** | 5.0+ | 极速开发体验，原生 ESM，HMR 快 |

### 2.2 UI 组件库

| 技术 | 版本 | 选型理由 |
|------|------|----------|
| **TDesign** | 1.0+ | 腾讯开源，企业级设计系统，组件丰富，设计现代，Vue 3 支持完善 |

**备选方案对比：**

| 组件库 | 优点 | 缺点 | 结论 |
|--------|------|------|------|
| TDesign | 设计现代、企业级、组件全 | 社区相对较小 | ✅ 选择 |
| Element Plus | 社区大、生态成熟 | 设计偏传统 | 备选 |
| Ant Design Vue | 设计优秀、组件丰富 | 风格偏蚂蚁金服 | 备选 |

### 2.3 状态管理

| 技术 | 版本 | 选型理由 |
|------|------|----------|
| **Pinia** | 2.1+ | Vue 官方推荐，更简洁的 API，TypeScript 友好，DevTools 支持 |

### 2.4 路由管理

| 技术 | 版本 | 选型理由 |
|------|------|----------|
| **Vue Router** | 4.2+ | Vue 官方路由，功能完善，支持路由守卫 |

### 2.5 HTTP 客户端

| 技术 | 版本 | 选型理由 |
|------|------|----------|
| **Axios** | 1.6+ | 功能完善，拦截器机制，广泛使用 |

### 2.6 工具库

| 技术 | 版本 | 用途 |
|------|------|------|
| dayjs | 1.11+ | 日期处理 |
| lodash-es | 4.17+ | 工具函数（按需引入） |
| @vueuse/core | 10.0+ | Vue Composition API 工具集 |

### 2.7 代码规范

| 工具 | 用途 |
|------|------|
| ESLint | 代码检查 |
| Prettier | 代码格式化 |
| Stylelint | CSS/Less 检查 |
| Husky | Git Hooks |
| lint-staged | 提交前检查 |

---

## 三、后端技术栈

### 3.1 核心框架

| 技术 | 版本 | 选型理由 |
|------|------|----------|
| **Spring Boot** | 3.2.x | Java 微服务首选框架，生态完善 |
| **Spring Cloud** | 2023.x | 微服务治理标准方案 |
| **Spring Cloud Alibaba** | 2023.x | 阿里巴巴微服务套件，国内生态好 |

### 3.2 微服务组件

| 组件 | 版本 | 用途 | 选型理由 |
|------|------|------|----------|
| **Nacos** | 2.3+ | 服务注册与配置中心 | 功能完善，国内主流选择 |
| **Sentinel** | 1.8+ | 流量控制与熔断 | 轻量级，控制台友好 |
| **Spring Cloud Gateway** | 4.1+ | API 网关 | 响应式，性能好，Spring 生态集成 |

### 3.3 认证授权

| 技术 | 版本 | 选型理由 |
|------|------|----------|
| **Sa-Token** | 1.37+ | 轻量级权限框架，API 简洁，功能完善（登录认证、权限认证、Session管理、OAuth2） |

**备选方案对比：**

| 框架 | 优点 | 缺点 | 结论 |
|------|------|------|------|
| Sa-Token | 轻量、API 简洁、功能全 | 社区相对较小 | ✅ 选择 |
| Spring Security | 功能强大、生态完善 | 配置复杂、学习曲线陡 | 备选 |
| Apache Shiro | 简单易用 | 功能相对较少 | 不考虑 |

### 3.4 数据访问

| 技术 | 版本 | 选型理由 |
|------|------|----------|
| **MyBatis-Plus** | 3.5+ | 简化 CRUD，代码生成器，分页插件 |
| **Druid** | 1.2+ | 阿里数据库连接池，监控完善 |
| **Flyway** | 9.0+ | 数据库版本管理 |

### 3.5 数据存储

| 技术 | 版本 | 用途 | 选型理由 |
|------|------|------|----------|
| **MySQL** | 8.0+ | 主数据库 | 成熟稳定，生态完善 |
| **Redis** | 7.0+ | 缓存/Session | 高性能，功能丰富 |

### 3.6 工具库

| 技术 | 版本 | 用途 |
|------|------|------|
| Hutool | 5.8+ | Java 工具类库 |
| Lombok | 1.18+ | 简化代码 |
| MapStruct | 1.5+ | 对象映射 |
| Knife4j | 4.3+ | API 文档（Swagger 增强） |

### 3.7 代码规范

| 工具 | 用途 |
|------|------|
| Alibaba Java Coding Guidelines | 代码检查 |
| Spotless | 代码格式化 |

---

## 四、基础设施

### 4.1 容器化

| 技术 | 版本 | 用途 |
|------|------|------|
| Docker | 24+ | 容器化 |
| Docker Compose | 2.20+ | 本地开发环境编排 |

### 4.2 监控告警（可选）

| 技术 | 版本 | 用途 |
|------|------|------|
| Prometheus | 2.45+ | 指标采集 |
| Grafana | 10.0+ | 监控面板 |

---

## 五、开发工具

| 工具 | 用途 |
|------|------|
| IntelliJ IDEA | 后端开发 IDE |
| VS Code / WebStorm | 前端开发 IDE |
| Apifox / Postman | API 测试 |
| DBeaver | 数据库管理 |
| Another Redis Desktop Manager | Redis 管理 |

---

## 六、依赖版本汇总

### 6.1 前端 package.json 核心依赖

```json
{
  "dependencies": {
    "vue": "^3.4.0",
    "vue-router": "^4.2.0",
    "pinia": "^2.1.0",
    "tdesign-vue-next": "^1.0.0",
    "axios": "^1.6.0",
    "dayjs": "^1.11.0",
    "lodash-es": "^4.17.0",
    "@vueuse/core": "^10.0.0"
  },
  "devDependencies": {
    "typescript": "^5.0.0",
    "vite": "^5.0.0",
    "@vitejs/plugin-vue": "^5.0.0",
    "eslint": "^8.0.0",
    "prettier": "^3.0.0",
    "stylelint": "^15.0.0",
    "husky": "^8.0.0",
    "lint-staged": "^14.0.0"
  }
}
```

### 6.2 后端 pom.xml 核心依赖

```xml
<properties>
    <java.version>17</java.version>
    <spring-boot.version>3.2.0</spring-boot.version>
    <spring-cloud.version>2023.0.0</spring-cloud.version>
    <spring-cloud-alibaba.version>2023.0.0</spring-cloud-alibaba.version>
    <mybatis-plus.version>3.5.5</mybatis-plus.version>
    <sa-token.version>1.37.0</sa-token.version>
    <hutool.version>5.8.24</hutool.version>
</properties>

<dependencies>
    <!-- Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Spring Cloud -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter</artifactId>
    </dependency>
    
    <!-- Spring Cloud Alibaba -->
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    </dependency>
    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
    </dependency>
    
    <!-- Sa-Token -->
    <dependency>
        <groupId>cn.dev33</groupId>
        <artifactId>sa-token-spring-boot3-starter</artifactId>
        <version>${sa-token.version}</version>
    </dependency>
    
    <!-- MyBatis-Plus -->
    <dependency>
        <groupId>com.baomidou</groupId>
        <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
        <version>${mybatis-plus.version}</version>
    </dependency>
    
    <!-- Hutool -->
    <dependency>
        <groupId>cn.hutool</groupId>
        <artifactId>hutool-all</artifactId>
        <version>${hutool.version}</version>
    </dependency>
</dependencies>
```

---

## 七、技术选型决策记录（ADR）

### ADR-001: 选择 Sa-Token 而非 Spring Security

**状态**: 已决定

**背景**: 需要一个认证授权框架来处理用户登录、权限控制。

**决策**: 选择 Sa-Token

**理由**:
1. API 更简洁，学习曲线低
2. 功能完善：登录认证、权限认证、Session 管理、OAuth2 都支持
3. 轻量级，不依赖 Spring Security
4. 中文文档完善

**影响**:
- 团队需要学习 Sa-Token API
- 社区资源相对 Spring Security 较少

---

### ADR-002: 选择 TDesign 而非 Element Plus

**状态**: 已决定

**背景**: 需要一个 Vue 3 组件库。

**决策**: 选择 TDesign

**理由**:
1. 设计系统更现代，符合企业级需求
2. 组件丰富，覆盖管理后台场景
3. 腾讯开源，长期维护有保障
4. TypeScript 支持完善

**影响**:
- 团队需要学习 TDesign 组件 API
- 遇到问题时社区资源相对较少

---

### ADR-003: MVP 阶段使用 Docker Compose 而非 Kubernetes

**状态**: 已决定

**背景**: 需要决定部署方案。

**决策**: MVP 阶段使用 Docker Compose

**理由**:
1. 降低运维复杂度，专注业务开发
2. 本地开发环境一致性更好
3. 后续可平滑迁移到 K8s

**影响**:
- 生产环境扩展性受限
- 需要后续规划 K8s 迁移方案

---

**文档维护者**: AI Assistant
**最后更新**: 2026-02-13
