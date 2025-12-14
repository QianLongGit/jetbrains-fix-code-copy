package com.github.qianlonggit.jetbrainsfixcodecopy.utils

import com.github.qianlonggit.jetbrainsfixcodecopy.services.ClipboardService
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile

/**
 * 编辑器上下文信息提取工具
 *
 * 用于从编辑器和文件中提取修复 Prompt 所需的上下文信息
 *
 * @author qianlonggit
 * @since 2025-12-13
 */
object EditorContext {

    /**
     * 提取编辑器上下文信息
     *
     * @param editor 当前编辑器
     * @param file 当前 PSI 文件
     * @return 包含所有必要信息的 FixContext 对象
     */
    fun extractContext(editor: Editor, file: PsiFile): FixContext {
        // 获取文件路径
        val filePath = extractFilePath(file)

        // 获取光标位置
        val caretOffset = editor.caretModel.offset

        // 获取文档
        val document = editor.document

        // 获取选中的文本
        val selectedText = extractSelectedText(editor)

        // 获取行号范围
        val (startLine, endLine) = extractLineRange(document, caretOffset, selectedText)

        // 获取诊断信息
        val diagnosticText = extractDiagnosticText(file, caretOffset)

        // 自动获取错误信息作为 userInput
        val userInput = extractErrorUserInput(editor, file)

        // 返回上下文信息
        return FixContext(
            filePath = filePath,
            startLine = startLine,
            endLine = endLine,
            diagnosticText = diagnosticText,
            selectedText = selectedText,
            userInput = userInput
        )
    }

    /**
     * 提取文件路径
     */
    private fun extractFilePath(file: PsiFile): String {
        val virtualFile = file.virtualFile
        return if (virtualFile != null) {
            virtualFile.path
        } else {
            // 如果没有虚拟文件，使用文件名
            file.name
        }
    }

    /**
     * 提取选中的文本
     */
    private fun extractSelectedText(editor: Editor): String {
        val selectionModel = editor.selectionModel
        return if (selectionModel.hasSelection()) {
            selectionModel.selectedText ?: ""
        } else {
            // 如果没有选中内容，获取当前行
            val caretOffset = editor.caretModel.offset
            val document = editor.document
            val lineNumber = document.getLineNumber(caretOffset)
            val lineStartOffset = document.getLineStartOffset(lineNumber)
            val lineEndOffset = document.getLineEndOffset(lineNumber)
            document.getText(TextRange(lineStartOffset, lineEndOffset)).trim()
        }
    }

    /**
     * 提取行号范围
     */
    private fun extractLineRange(
        document: com.intellij.openapi.editor.Document,
        caretOffset: Int,
        selectedText: String
    ): Pair<Int, Int> {
        // 如果有选中内容，计算选中内容的行范围
        if (selectedText.isNotEmpty()) {
            // 这里需要实际的选中范围，暂时使用光标所在行
            val lineNumber = document.getLineNumber(caretOffset)
            return Pair(lineNumber + 1, lineNumber + 1) // 转换为 1-based
        } else {
            // 没有选中内容，使用光标所在行
            val lineNumber = document.getLineNumber(caretOffset)
            return Pair(lineNumber + 1, lineNumber + 1) // 转换为 1-based
        }
    }

    /**
     * 提取错误用户输入
     * 优先获取光标位置的自动错误信息，降级使用剪贴板内容
     */
    private fun extractErrorUserInput(editor: Editor, file: PsiFile): String {
        // 首先尝试获取自动错误信息
        try {
            val autoErrorInfo = DiagnosticUtils.getErrorInfoAtCursor(file, editor)
            if (autoErrorInfo.isNotBlank() && !autoErrorInfo.equals("代码可能存在潜在问题，请检查语法、类型、逻辑等方面")) {
                return autoErrorInfo
            }
        } catch (e: Exception) {
            // 如果自动获取失败，记录日志但不中断流程
            com.intellij.openapi.diagnostic.Logger.getInstance(EditorContext::class.java)
                .warn("自动获取错误信息失败，降级使用剪贴板", e)
        }

        // 降级方案：使用剪贴板内容
        return ClipboardService.getInstance().getClipboardContent()?.trim() ?: ""
    }

    /**
     * 提取诊断文本
     */
    private fun extractDiagnosticText(file: PsiFile, offset: Int): String {
        val diagnostics = DiagnosticUtils.getDiagnosticsAtOffset(file, offset)
        return if (diagnostics.isNotEmpty()) {
            // 只取第一个诊断信息，避免信息过多
            diagnostics.first().message
        } else {
            // 如果没有诊断信息，返回通用提示
            "代码可能存在潜在问题，请检查语法、类型、逻辑等方面"
        }
    }
}

/**
 * 修复上下文数据类
 *
 * 包含生成修复 Prompt 所需的所有信息
 *
 * @param filePath 文件路径
 * @param startLine 起始行号（1-based）
 * @param endLine 结束行号（1-based）
 * @param diagnosticText 诊断信息文本
 * @param selectedText 选中的代码文本
 * @param userInput 用户输入（固定为空）
 */
data class FixContext(
    val filePath: String,
    val startLine: Int,
    val endLine: Int,
    val diagnosticText: String,
    val selectedText: String,
    val userInput: String = ""
)
