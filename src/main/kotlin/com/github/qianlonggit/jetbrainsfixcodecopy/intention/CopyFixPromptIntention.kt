package com.github.qianlonggit.jetbrainsfixcodecopy.intention

import com.github.qianlonggit.jetbrainsfixcodecopy.services.ClipboardService
import com.github.qianlonggit.jetbrainsfixcodecopy.services.PromptTemplateService
import com.github.qianlonggit.jetbrainsfixcodecopy.utils.EditorContext
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

/**
 * 复制修复 Prompt 意图动作
 *
 * 功能：在代码出现错误或警告时，通过 Alt+Enter 菜单提供"复制修复 Prompt"选项
 *
 * @author qianlonggit
 * @since 2025-12-13
 */
class CopyFixPromptIntention : IntentionAction {

    companion object {
        private const val MENU_TEXT = "复制修复 Prompt"
        private const val FAMILY_NAME = "Copy Fix Prompt"
    }

    /**
     * 获取在意图菜单中显示的文本
     */
    override fun getText(): String = MENU_TEXT

    /**
     * 获取意图动作的家族名称，用于分组显示
     */
    override fun getFamilyName(): String = FAMILY_NAME

    /**
     * 检查该意图动作是否在当前位置可用
     *
     * @param project 当前项目
     * @param editor 当前编辑器
     * @param file 当前文件
     * @return 在任何代码文件中都返回 true，让用户可以随时使用
     */
    override fun isAvailable(project: Project, editor: Editor, file: PsiFile): Boolean {
        // 只检查基本条件，不检查诊断信息
        // 让用户可以随时使用这个功能，即使没有明显的错误

        // 如果文件为空，不显示
        if (file.textLength == 0) {
            return false
        }

        // 检查光标是否在有效范围内
        val offset = editor.caretModel.offset
        if (offset < 0 || offset > file.textLength) {
            return false
        }

        // 确保这是一个源代码文件（不是二进制文件等）
        return file.fileType.isBinary.not()
    }

    /**
     * 执行意图动作
     *
     * @param project 当前项目
     * @param editor 当前编辑器
     * @param file 当前文件
     */
    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        try {
            // 提取编辑器上下文信息
            val context = EditorContext.extractContext(editor, file)

            // 生成修复 Prompt
            val prompt = PromptTemplateService.generatePrompt(context)

            // 复制到剪贴板
            ClipboardService.copyToClipboard(prompt, project)

        } catch (e: Exception) {
            // 记录错误日志
            com.intellij.openapi.diagnostic.Logger.getInstance(
                CopyFixPromptIntention::class.java
            ).error("Failed to copy fix prompt", e)
        }
    }

    /**
     * 是否需要在写操作中执行
     * 由于我们只是读取信息和复制到剪贴板，不需要修改代码，返回 false
     */
    override fun startInWriteAction(): Boolean = false
}
