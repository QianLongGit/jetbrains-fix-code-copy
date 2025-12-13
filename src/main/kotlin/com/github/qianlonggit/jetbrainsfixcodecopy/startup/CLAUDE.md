[根目录](../../../../CLAUDE.md) > [src](../../../) > [main](../../../) > [kotlin](../../../) > [com.github.qianlonggit.jetbrainsfixcodecopy](../../) > [startup](../) >
**startup**

# Startup 模块

## 模块职责

该模块负责插件启动时的初始化活动，确保插件在 IDE 启动后正确初始化。目前包含：

- `MyProjectActivity`: 项目启动活动实现

## 入口与启动

### 当前文件

- `MyProjectActivity.kt`: 项目启动活动，在项目打开时执行初始化

## 对外接口

### MyProjectActivity

- 继承自 `StartupActivity`
- 在 `runActivity()` 中执行启动逻辑
- 通过 `project` 参数访问项目实例

## 关键依赖与配置

### IntelliJ Platform 启动活动

- 使用 `PostStartupActivity` 注解指定在 IDE 启动后执行
- 自动注册到项目生命周期

### 依赖项

- `com.intellij.openapi.startup.StartupActivity`: 启动活动基类
- `com.intellij.openapi.project.Project`: 项目接口

## 数据模型

该模块不包含数据模型，主要处理初始化逻辑。

## 测试与质量

### 测试建议

- 测试启动活动的初始化逻辑
- 验证在项目打开时正确执行
- 确保不会影响 IDE 启动性能

### 最佳实践

- 保持启动逻辑轻量级
- 避免阻塞 UI 线程
- 正确处理异常情况

## 常见问题 (FAQ)

1. **Q: 启动活动和扩展点有什么区别？**
   A: 启动活动在 IDE 启动后执行一次，扩展点提供持续的功能支持。

2. **Q: 如何控制启动顺序？**
   A: 使用 `@PostStartupActivity` 的 `runAfter` 参数指定依赖关系。

## 相关文件清单

- `MyProjectActivity.kt` - 项目启动活动
- `../MyBundle.kt` - 资源访问
- `../services/MyProjectService.kt` - 项目服务
- `../../resources/META-INF/plugin.xml` - 插件配置

## 变更记录 (Changelog)

### 2025-12-13 14:38:49

- 创建模块文档
- 分析启动流程
- 规划初始化需求
