# 個人博客

## 项目简介
本项目是一个基于 Spring Boot 3.5 的博客后台管理与展示系统，完成从 JDK 8 向 JDK 17 的迁移，并引入 MyBatis-Plus 统一 ORM 风格。核心功能包括文章管理、分类管理、标签管理、评论审核、友情链接等。

## 技术栈
- **后端框架**：Spring Boot 3.5.x、Spring MVC
- **ORM**：MyBatis-Plus 3.5.8
- **数据库**：MySQL 8.x，使用 HikariCP 连接池
- **安全/工具**：JWT 登录鉴权、Hutool 验证码、Lombok

## 目录结构
```
src/main/java/com/zixin/blogplatform
├─ config/       # 常量、MyBatisPlus配置、拦截器注册等
├─ controller/   # admin（后台管理接口）、common（验证码）等
├─ dao/          # MyBatis-Plus Mapper 接口
├─ entity/       # 数据库实体
├─ interceptor/  # 登录鉴权拦截器
├─ service/      # 业务接口
│  └─ impl/      # 业务实现
└─ util/         # 通用工具类（分页、响应包装、文本处理等）
```

## 核心功能说明
1. **文章管理**
   - 新建 / 编辑 / 删除 / 列表 / 热榜
   - 保存文章时支持多标签、多分类，标签自动去重并统一写入 `blog_tag`、`blog_tag_relation`
   - 使用手动事务 (`PlatformTransactionManager`) 确保文章、标签、关系写入的原子性

2. **分类管理**
   - MyBatis-Plus 通用 Service 实现
   - 保存文章时会校验 `blogCategoryId` 是否真实存在，避免脏数据

3. **标签管理**
   - `TagServiceImpl` 使用 MyBatis-Plus 分页，保留旧的 PageQueryUtil 接口兼容后台页面
   - 删除标签前会检查是否存在关联关系，确保不会误删

4. **评论管理**
   - 自定义 CommentService，支持嵌套评论展示与删除
   - 只允许合法邮箱、可选合法网址的评论创建

5. **登录与注册**
   - `/admin/login` 和 `/admin/register` 采用 REST 风格接口，返回统一 JSON
   - 拦截器 `AdminLoginInterceptor` 对后台路径进行鉴权，仅放行登录、注册、静态资源

6. **文件上传**
   - `UploadController` 负责处理后台富文本上传，保存到指定目录并返回访问 URL

## 启动与使用
1. **环境要求**
   - JDK 17+
   - MySQL 8.x
2. **配置数据库**
   - 在 `application.yml` 配置 `spring.datasource`（URL/用户名/密码）
3. **运行**
   - `mvn spring-boot:run` 或通过 IDE 直接运行 `BlogPlatformApplication`
4. **后台访问**
   - 登录接口：`POST http://localhost:28083/admin/login`
   - 注册接口：`POST http://localhost:28083/admin/register`
   - 后台页面入口（需登录）：`http://localhost:28083/admin`

## 其他说的
1. 这个是前后端分离的项目，这里是后端项目，、
2. 最开始考虑的是在2核2G的服务器运行，所以没写太多复杂的功能，后面越写越感觉这个后端项目不是很有必要，目前的各个接口已经调通了，但是功能方面只有博客功能，如果有需要可以自己再拓展拓展
