[根目录](../../../CLAUDE.md) > [src](../../../) > [main](../../../) > [kotlin](../../../) > [com.github.qianlonggit.jetbrainsfixcodecopy](../) > **jetbrainsfixcodecopy**

# jetbrainsfixcodecopy 主包

## 模块职责

这是插件的核心包，包含主要的入口点和基础配置。目前包含：
- `MyBundle`: 国际化资源管理器
- 插件的主要类和接口定义

## 入口与启动

### 当前文件
- `MyBundle.kt`: 提供插件国际化支持，管理消息资源

## 对外接口

目前该模块主要提供内部支持：
- 国际化消息访问接口
- 资源管理功能

## 关键依赖与配置

### 依赖项
- `com.intellij.DynamicBundle`: IntelliJ 平台动态资源包
- `org.jetbrains.annotations`: 注解支持

### 配置文件
- 资源文件: `src/main/resources/messages/MyBundle.properties`

## 数据模型

该模块目前没有特定的数据模型，主要作为资源访问层。

## 测试与质量

### 测试覆盖
- 需要为 `MyBundle` 添加单元测试
- 测试资源加载和消息格式化

### 代码质量
- 遵循 Kotlin 编码规范
- 使用适当的注解提高代码可读性

## 常见问题 (FAQ)

1. **Q: 如何添加新的国际化消息？**
   A: 在 `MyBundle.properties` 中添加新的键值对，然后使用 `MyBundle.message()` 访问。

2. **Q: 支持多语言吗？**
   A: 是的，可以通过创建对应语言的 properties 文件实现多语言支持。

## 相关文件清单

- `MyBundle.kt` - 资源包管理器
- `../../resources/messages/MyBundle.properties` - 默认资源文件
- `services/` - 服务层实现
- `startup/` - 启动活动实现
- `toolWindow/` - 工具窗口实现

## 变更记录 (Changelog)

### 2025-12-13 14:38:49
- 创建模块文档
- 分析现有模板代码结构
- 规划核心功能实现位置
