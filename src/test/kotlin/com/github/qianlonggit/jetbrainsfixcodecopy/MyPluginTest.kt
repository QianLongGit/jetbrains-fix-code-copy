package com.github.qianlonggit.jetbrainsfixcodecopy

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.github.qianlonggit.jetbrainsfixcodecopy.intention.CopyFixPromptIntention

/**
 * 插件功能测试
 */
class MyPluginTest : BasePlatformTestCase() {

    /**
     * 测试 CopyFixPromptIntention 是否可以正确识别并显示
     */
    fun testCopyFixPromptIntentionAvailable() {
        // 创建一个有语法错误的 Java 代码
        val code = """
            public class Test {
                public void method() {
                    System.out.println("hello"  // 缺少分号
                }
            }
        """.trimIndent()

        // 配置文件内容
        val psiFile = myFixture.configureByText("Test.java", code)

        // 创建意图动作实例
        val intention = CopyFixPromptIntention()

        // 测试意图动作是否可用
        assertTrue("Intention should be available for code with potential issues",
                   intention.isAvailable(project, myFixture.editor, psiFile))
    }

    /**
     * 测试 IntentionAction 的文本和族名
     */
    fun testIntentionText() {
        val intention = CopyFixPromptIntention()

        assertEquals("复制修复 Prompt", intention.getText())
        assertEquals("Copy Fix Prompt", intention.getFamilyName())
        assertFalse("Should not require write action", intention.startInWriteAction())
    }

    /**
     * 测试在空文件中意图动作不可用
     */
    fun testIntentionNotAvailableInEmptyFile() {
        val psiFile = myFixture.configureByText("Empty.java", "")
        val intention = CopyFixPromptIntention()

        assertFalse("Intention should not be available in empty file",
                   intention.isAvailable(project, myFixture.editor, psiFile))
    }

    /**
     * 测试在注释中意图动作的可用性
     */
    fun testIntentionInComment() {
        val code = """
            // This is a comment line
            public class Test {}
        """.trimIndent()

        val psiFile = myFixture.configureByText("Test.java", code)
        val intention = CopyFixPromptIntention()

        // 即使是注释，也应该可用（提供分析功能）
        assertTrue("Intention should be available in comments",
                   intention.isAvailable(project, myFixture.editor, psiFile))
    }
}
