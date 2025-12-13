package com.github.qianlonggit.jetbrainsfixcodecopy.templates

import com.github.qianlonggit.jetbrainsfixcodecopy.utils.FixContext

/**
 * 默认 Prompt 模板
 *
 * 定义用于生成修复提示的默认模板格式
 *
 * @author qianlonggit
 * @since 2025-12-13
 */
object DefaultPromptTemplate {

    /**
     * 默认 Prompt 模板
     */
    private const val DEFAULT_TEMPLATE = """
修复文件路径 ${'$'}{filePath} 中 ${'$'}{startLine}-${'$'}{endLine} 行的代码问题：

${'$'}{userInput}

```
${'$'}{selectedText}
```

请：
1. 解决上述列出的所有已发现问题（如有）
2. 指出其他潜在的错误或问题
3. 提供修正后的代码
4. 说明修复内容及原因
"""

    /**
     * 简化版 Prompt 模板（用于没有选中文本的情况）
     */
    private const val SIMPLE_TEMPLATE = """
修复文件路径 ${'$'}{filePath} 中 ${'$'}{startLine}-${'$'}{endLine} 行的代码问题：

${'$'}{userInput}

请：
1. 解决上述列出的所有已发现问题（如有）
2. 指出其他潜在的错误或问题
3. 提供修正后的代码
4. 说明修复内容及原因
"""

    /**
     * 渲染模板
     *
     * @param context 修复上下文信息
     * @return 渲染后的 Prompt 文本
     */
    fun render(context: FixContext): String {
        // 根据是否有选中文本选择模板
        val template = if (context.selectedText.isNotBlank()) {
            DEFAULT_TEMPLATE
        } else {
            SIMPLE_TEMPLATE
        }

        return renderTemplate(template, context)
    }

    /**
     * 渲染指定的模板
     *
     * @param template 模板字符串
     * @param context 修复上下文信息
     * @return 渲染后的文本
     */
    private fun renderTemplate(template: String, context: FixContext): String {
        // 处理可能的 null 值
        val safeContext = context.copy(
            selectedText = context.selectedText.ifBlank { "// 无选中代码" }
        )

        // 执行变量替换
        var result = template
            .replace("\${filePath}", safeContext.filePath)
            .replace("\${startLine}", safeContext.startLine.toString())
            .replace("\${endLine}", safeContext.endLine.toString())
            .replace("\${selectedText}", safeContext.selectedText)
            .replace("\${userInput}", safeContext.userInput)

        // 清理多余的空行
        result = result.replace(Regex("\n{3,}"), "\n\n")

        // 移除首尾空白
        result = result.trim()

        return result
    }

    /**
     * 获取模板变量说明
     */
    fun getVariableDescriptions(): Map<String, String> {
        return mapOf(
            "filePath" to "文件路径",
            "startLine" to "起始行号",
            "endLine" to "结束行号",
            "selectedText" to "选中的代码文本",
            "userInput" to "用户输入（从剪贴板读取的错误信息）"
        )
    }
}
