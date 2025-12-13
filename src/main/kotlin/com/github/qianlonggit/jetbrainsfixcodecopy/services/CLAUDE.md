[根目录](../../../../CLAUDE.md) > [src](../../../) > [main](../../../) > [kotlin](../../../) > [com.github.qianlonggit.jetbrainsfixcodecopy](../../) > [services](../) >
**services**

# Services 模块

## 模块职责

该模块负责实现项目级服务，提供插件的核心业务逻辑和持久化功能。目前包含：

- `MyProjectService`: 项目服务实现示例

## 入口与启动

### 当前文件

- `MyProjectService.kt`: 项目服务实现，提供项目级别的功能支持

## 对外接口

### MyProjectService

- 构造函数：接收 `Project` 实例
- `getRandomNumber()`: 返回 1-100 的随机数（示例功能）

## 关键依赖与配置

### IntelliJ Platform 服务

- 使用 `@Service(Service.Level.PROJECT)` 注解注册为项目级服务
- 自动依赖注入到项目生命周期中

### 依赖项

- `com.intellij.openapi.components.Service`: 服务框架
- `com.intellij.openapi.diagnostic`: 日志功能
- `com.intellij.openapi.project.Project`: 项目接口

## 数据模型

该模块目前没有持久化数据模型，主要提供运行时服务。

## 测试与质量

### 测试建议

- 应为项目服务编写单元测试
- 测试服务生命周期管理
- 验证服务功能正确性

### 代码质量提示

- 注意日志记录的合理性
- 避免在服务中存储可变状态

## 常见问题 (FAQ)

1. **Q: 如何访问项目服务？**
   A: 使用 `project.getService(MyProjectService::class.java)`

2. **Q: 服务何时初始化？**
   A: 服务在首次访问时懒加载初始化

## 相关文件清单

- `MyProjectService.kt` - 项目服务实现
- `../MyBundle.kt` - 资源访问
- `../startup/MyProjectActivity.kt` - 项目启动活动
- `../../resources/META-INF/plugin.xml` - 插件配置

## 变更记录 (Changelog)

### 2025-12-13 14:38:49

- 创建模块文档
- 识别模板代码
- 规划服务扩展方向
