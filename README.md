# JetBrains Fix Code Copy

![Build](https://github.com/QianLongGit/jetbrains-fix-code-copy/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/0.0.1.svg)](https://plugins.jetbrains.com/plugin/0.0.1)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/0.0.1.svg)](https://plugins.jetbrains.com/plugin/0.0.1)

<!-- Plugin description -->
<p><strong>Fix Code Copy</strong> - Easily Copy Code Issues to AI Tools</p>
<p>When you encounter code errors or need optimization, simply press Alt+Enter and select "Copy Fix Prompt" to generate a structured prompt containing file path, line number, and error information. Works perfectly with AI tools like Claude Code, ChatGPT, and more, making code fixing easier than ever!</p>
<p><em>Key Features:</em></p>
<ul>
    <li>✅ One-click copy of structured fix prompts</li>
    <li>✅ Automatically extract file path and line number</li>
    <li>✅ Support for all languages including Java, Kotlin, Python, JavaScript</li>
    <li>✅ Intelligent syntax error detection</li>
</ul>
<!-- Plugin description end -->

## 📋 功能介绍

当您在 IntelliJ IDEA 或其他 JetBrains IDE 中遇到代码问题时，只需将光标放在问题代码处，按 `Alt+Enter`，选择"复制修复 Prompt"
，即可生成结构化的修复提示并复制到剪贴板。然后您可以轻松地将这个提示粘贴到 Claude Code、ChatGPT 等 AI 工具中获取代码修复建议。

### ✨ 主要特性

- 🎯 **一键复制**：通过 Alt+Enter 菜单快速复制修复提示
- 📝 **结构化模板**：生成包含文件路径、行号、诊断信息的完整提示
- 🌍 **多语言支持**：支持 Java、Kotlin、Python、JavaScript 等所有语言
- 🔧 **智能识别**：自动检测语法错误并提供相关提示
- 📋 **剪贴板集成**：自动复制到系统剪贴板，方便粘贴使用

## 🚀 安装方法

### 手动安装

1. 下载最新的插件包：[jetbrains-fix-code-copy-0.0.1.zip](build/distributions/jetbrains-fix-code-copy-0.0.1.zip)
2. 打开 IntelliJ IDEA
3. 进入 `File` > `Settings` > `Plugins`
4. 点击齿轮图标，选择 `Install Plugin from Disk...`
5. 选择下载的 zip 文件

## 📖 使用方法

### 基本使用

1. 在代码编辑器中，将光标放在有问题或需要分析的代码行
2. 按 `Alt+Enter`（或 `⌥⏎` on Mac）
3. 在弹出的意图菜单中选择"复制修复 Prompt"
4. 提示已复制到剪贴板
5. 将内容粘贴到 AI 工具（如 Claude Code）

### 快捷键

- **Alt+Enter**：打开意图菜单
- **选择复制修复 Prompt**：复制格式化的修复提示

## 🛠️ 开发构建

### 环境要求

- **JDK**: 21 或更高版本
- **IntelliJ IDEA**: 2025.2.5 或更高版本
- **Gradle**: 8.0 或更高版本

### 构建步骤

```bash
# 克隆仓库
git clone https://github.com/qianlonggit/jetbrains-fix-code-copy.git
cd jetbrains-fix-code-copy

# 构建插件
./gradlew buildPlugin

# 运行测试
./gradlew test

# 在开发环境中运行
./gradlew runIde
```

### 开发流程

1. Fork 本仓库
2. 创建功能分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 创建 Pull Request

### 代码规范

- 遵循 [Kotlin 编码规范](https://kotlinlang.org/docs/coding-conventions.html)
- 使用 4 空格缩进
- 函数名使用 camelCase
- 类名使用 PascalCase

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🙏 致谢

- [IntelliJ Platform SDK](https://plugins.jetbrains.com/docs/intellij/) - 强大的插件开发框架
- 所有贡献者和用户的支持

如果这个插件对您有帮助，请给个 ⭐ Star 支持一下！
