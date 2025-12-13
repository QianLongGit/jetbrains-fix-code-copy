package com.github.qianlonggit.jetbrainsfixcodecopy

import com.github.qianlonggit.jetbrainsfixcodecopy.templates.DefaultPromptTemplate
import com.github.qianlonggit.jetbrainsfixcodecopy.utils.FixContext
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

/**
 * 测试 DefaultPromptTemplate 的变量替换功能
 */
class DefaultPromptTemplateTest {

    @Test
    fun testTemplateVariableReplacement() {
        // 创建测试上下文
        val context = FixContext(
            filePath = "/src/main/java/Test.java",
            startLine = 10,
            endLine = 15,
            diagnosticText = "缺少分号",
            selectedText = "System.out.println(\"hello\")",
            userInput = ""
        )

        // 渲染模板
        val result = DefaultPromptTemplate.render(context)

        // 验证变量是否被正确替换
        assertTrue(result.contains("/src/main/java/Test.java"))
        assertTrue(result.contains("10-15"))
        assertTrue(result.contains("缺少分号"))
        assertTrue(result.contains("System.out.println(\"hello\")"))

        // 确保模板变量不被替换
        assertFalse(result.contains("\${filePath}"))
        assertFalse(result.contains("\${startLine}"))
        assertFalse(result.contains("\${diagnosticText}"))
        assertFalse(result.contains("\${selectedText}"))
    }

    @Test
    fun testTemplateWithEmptySelection() {
        val context = FixContext(
            filePath = "/src/test.py",
            startLine = 5,
            endLine = 5,
            diagnosticText = "语法错误",
            selectedText = "",
            userInput = ""
        )

        val result = DefaultPromptTemplate.render(context)

        // 即使没有选中内容，也应该包含文件信息
        assertTrue(result.contains("/src/test.py"))
        assertTrue(result.contains("5-5"))
        assertTrue(result.contains("语法错误"))
    }

    @Test
    fun testTemplateHandlesNullDiagnostic() {
        val context = FixContext(
            filePath = "/test.js",
            startLine = 1,
            endLine = 1,
            diagnosticText = "",
            selectedText = "let x = 1",
            userInput = ""
        )

        val result = DefaultPromptTemplate.render(context)

        // 应该提供默认的诊断信息
        assertTrue(result.contains("代码可能存在潜在问题"))
    }
}
