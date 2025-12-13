[根目录](../../../../CLAUDE.md) > [src](../../../) > [main](../../../) > [kotlin](../../../) > [com.github.qianlonggit.jetbrainsfixcodecopy](../../) > [toolWindow](../) >
**toolWindow**

# ToolWindow 模块

## 模块职责

该模块负责实现 IntelliJ IDEA 的工具窗口，为插件提供 UI 界面。目前包含：

- `MyToolWindowFactory`: 工具窗口工厂实现

## 入口与启动

### 当前文件

- `MyToolWindowFactory.kt`: 工具窗口工厂，创建和管理工具窗口实例

## 对外接口

### MyToolWindowFactory

- 实现 `ToolWindowFactory` 接口
- 在 `createToolWindowContent()` 中创建窗口内容
- 接收 `project` 和 `toolWindow` 参数

## 关键依赖与配置

### IntelliJ Platform 工具窗口

- 在 `plugin.xml` 中注册扩展点
- 支持停靠和浮动显示
- 集成到 IDE 的工具窗口管理

### 依赖项

- `com.intellij.openapi.wm.ToolWindowFactory`: 工具窗口工厂接口
- `com.intellij.openapi.project.Project`: 项目接口
- `com.intellij.ui.content.ContentFactory`: 内容工厂

## 数据模型

该模块主要负责 UI 实现，数据模型由其他模块提供。

## 测试与质量

### 测试建议

- 使用 Robot 框架进行 UI 测试
- 测试工具窗口的创建和销毁
- 验证用户交互功能

### UI/UX 考虑

- 遵循 IntelliJ UI 设计指南
- 确保响应式布局
- 提供键盘快捷键支持

## 常见问题 (FAQ)

1. **Q: 如何自定义工具窗口图标？**
   A: 在 `plugin.xml` 中通过 `icon` 属性指定。

2. **Q: 工具窗口可以同时打开多个吗？**
   A: 不行，每个工具窗口 ID 只能有一个实例。

## 相关文件清单

- `MyToolWindowFactory.kt` - 工具窗口工厂
- `../MyBundle.kt` - 资源访问
- `../../resources/META-INF/plugin.xml` - 插件配置
- `../services/MyProjectService.kt` - 项目服务

## 变更记录 (Changelog)

### 2025-12-13 14:38:49

- 创建模块文档
- 分析 UI 框架
- 规划界面需求
