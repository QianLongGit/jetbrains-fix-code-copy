package com.github.qianlonggit.jetbrainsfixcodecopy.utils

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerEx
import com.intellij.codeInsight.daemon.impl.HighlightInfo
import com.intellij.codeInsight.daemon.impl.HighlightInfoType
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReference
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.Range
import java.util.concurrent.TimeUnit

/**
 * 诊断信息工具类
 *
 * 用于提取代码编辑器中的错误、警告等诊断信息
 *
 * @author qianlonggit
 * @since 2025-12-13
 */
object DiagnosticUtils {

    private val logger = com.intellij.openapi.diagnostic.Logger.getInstance(DiagnosticUtils::class.java)

    /**
     * 获取指定偏移位置的诊断信息
     *
     * @param file 当前 PSI 文件
     * @param offset 光标偏移位置
     * @return 诊断信息列表，包含错误和警告
     */
    fun getDiagnosticsAtOffset(file: PsiFile, offset: Int): List<DiagnosticInfo> {
        val diagnostics = mutableListOf<DiagnosticInfo>()

        try {
            // 获取文档
            val document = PsiDocumentManager.getInstance(file.project).getDocument(file) ?: return emptyList()

            // 获取当前行信息
            val lineNumber = document.getLineNumber(offset)
            val lineStartOffset = document.getLineStartOffset(lineNumber)
            val lineEndOffset = document.getLineEndOffset(lineNumber)
            val lineText = document.getText(TextRange(lineStartOffset, lineEndOffset))

            // 方法1：尝试使用 DaemonCodeAnalyzer 获取高亮信息
            try {
                val highlightInfos = getHighlightInfos(file, offset)
                if (highlightInfos.isNotEmpty()) {
                    highlightInfos.forEach { info ->
                        diagnostics.add(
                            DiagnosticInfo(
                                type = when {
                                    info.severity === com.intellij.lang.annotation.HighlightSeverity.ERROR -> DiagnosticType.ERROR
                                    info.severity === com.intellij.lang.annotation.HighlightSeverity.WARNING -> DiagnosticType.WARNING
                                    info.severity === com.intellij.lang.annotation.HighlightSeverity.WEAK_WARNING -> DiagnosticType.WARNING
                                    else -> DiagnosticType.INFO
                                },
                                message = info.description ?: "代码问题",
                                startOffset = info.startOffset,
                                endOffset = info.endOffset,
                                lineNumber = lineNumber + 1
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                logger.debug("Failed to get highlight infos", e)
            }

            // 方法2：如果没有获取到诊断信息，添加通用的分析提示
            if (diagnostics.isEmpty() && lineText.trim().isNotEmpty()) {
                diagnostics.add(
                    DiagnosticInfo(
                        type = DiagnosticType.INFO,
                        message = "代码分析 - 检查语法、类型、逻辑等方面",
                        startOffset = lineStartOffset,
                        endOffset = lineEndOffset,
                        lineNumber = lineNumber + 1
                    )
                )
            }

        } catch (e: Exception) {
            logger.debug("Failed to get diagnostics", e)
        }

        return diagnostics
    }

    /**
     * 尝试获取高亮信息
     * 使用 DaemonCodeAnalyzer 获取实际的错误和警告信息
     */
    private fun getHighlightInfos(file: PsiFile, offset: Int): List<HighlightInfo> {
        logger.info("尝试获取高亮信息，文件: ${file.name}, 偏移: $offset")

        try {
            // 方法1: 尝试使用 DaemonCodeAnalyzer 获取文件状态
            val project = file.project
            val daemonCodeAnalyzer = DaemonCodeAnalyzer.getInstance(project)

            logger.debug("成功获取 DaemonCodeAnalyzer 实例")

            // 确保文档已保存和 PSI 已同步
            val documentManager = PsiDocumentManager.getInstance(project)
            val document = documentManager.getDocument(file)

            if (document != null) {
                logger.debug("成功获取文档，长度: ${document.textLength}")

                // 等待 PSI 和文档同步
                documentManager.commitDocument(document)

                // 获取当前行信息
                val lineNumber = document.getLineNumber(offset)
                val lineStartOffset = document.getLineStartOffset(lineNumber)
                val lineEndOffset = document.getLineEndOffset(lineNumber)

                logger.debug("行号: ${lineNumber + 1}, 行范围: [$lineStartOffset, $lineEndOffset]")

                // 方法2: 通过编辑器获取高亮信息
                val highlights = mutableListOf<HighlightInfo>()

                // 使用简单的行范围扫描来查找错误
                try {
                    // 检查 PSI 错误元素
                    val psiErrorElements = PsiTreeUtil.getChildrenOfTypeAsList(file, com.intellij.psi.PsiErrorElement::class.java)

                    logger.debug("找到 ${psiErrorElements.size} 个 PSI 错误元素")

                    for (errorElement in psiErrorElements) {
                        val errorRange = errorElement.textRange
                        if (errorRange != null && errorRange.containsOffset(offset)) {
                            val highlightInfo = createHighlightInfoFromPsiError(errorElement, file, offset)
                            if (highlightInfo != null) {
                                highlights.add(highlightInfo)
                                logger.info("从 PSI 错误元素创建高亮信息: ${errorElement.errorDescription}")
                            }
                        }
                    }
                } catch (e: Exception) {
                    logger.warn("扫描 PSI 错误元素时出错", e)
                }

                // 方法3: 扫描常见错误模式（备用方案）
                if (highlights.isEmpty()) {
                    val lineText = document.getText(TextRange(lineStartOffset, lineEndOffset))
                    val syntaxIssues = detectSyntaxIssues(lineText, lineNumber + 1, lineStartOffset, lineEndOffset)

                    logger.debug("检测到 ${syntaxIssues.size} 个语法问题")

                    for (issue in syntaxIssues) {
                        val highlightInfo = createHighlightInfoFromDiagnostic(issue, file)
                        if (highlightInfo != null) {
                            highlights.add(highlightInfo)
                        }
                    }
                }

                return highlights

            } else {
                logger.warn("无法获取文档对象")
            }

        } catch (e: Exception) {
            logger.error("获取高亮信息时发生异常", e)
        }

        logger.debug("返回空的高亮信息列表")
        return emptyList()
    }

    /**
     * 从 PSI 错误元素创建 HighlightInfo
     */
    private fun createHighlightInfoFromPsiError(
        errorElement: com.intellij.psi.PsiErrorElement, file: PsiFile, offset: Int
    ): HighlightInfo? {
        try {
            val textRange = errorElement.textRange ?: return null
            val description = errorElement.errorDescription ?: "语法错误"

            return HighlightInfo.newHighlightInfo(
                HighlightInfoType.ERROR
            )
                .range(textRange)
                .description(description)
                .create()
        } catch (e: Exception) {
            logger.warn("从 PSI 错误创建 HighlightInfo 时出错", e)
            return null
        }
    }

    /**
     * 从 DiagnosticInfo 创建 HighlightInfo
     */
    private fun createHighlightInfoFromDiagnostic(diagnostic: DiagnosticInfo, file: PsiFile): HighlightInfo? {
        try {
            val severity = when (diagnostic.type) {
                DiagnosticType.ERROR -> HighlightSeverity.ERROR
                DiagnosticType.WARNING -> HighlightSeverity.WARNING
                DiagnosticType.INFO -> HighlightSeverity.INFORMATION
                DiagnosticType.WEAK_WARNING -> HighlightSeverity.WEAK_WARNING
            }

            val highlightType = when (diagnostic.type) {
                DiagnosticType.ERROR -> HighlightInfoType.ERROR
                DiagnosticType.WARNING -> HighlightInfoType.WARNING
                DiagnosticType.INFO -> HighlightInfoType.INFORMATION
                DiagnosticType.WEAK_WARNING -> HighlightInfoType.WEAK_WARNING
            }

            return HighlightInfo.newHighlightInfo(highlightType)
                .range(TextRange(diagnostic.startOffset, diagnostic.endOffset))
                .description(diagnostic.message)
                .severity(severity)
                .create()
        } catch (e: Exception) {
            logger.warn("创建 HighlightInfo 时出错", e)
            return null
        }
    }

    /**
     * 获取光标位置的错误信息（主要方法）
     * 使用正确的 DaemonCodeAnalyzerEx API 获取 IDE 错误信息
     */
    fun getErrorInfoAtCursor(file: PsiFile, editor: Editor): String {
        val offset = editor.caretModel.offset
        logger.info("获取光标位置错误信息，偏移: $offset")

        try {
            // 方法1：使用 DaemonCodeAnalyzerEx 获取精确的高亮信息
            val highlightInfos = getHighlightInfosAtOffset(file, offset)
            if (highlightInfos.isNotEmpty()) {
                val errorMessages = highlightInfos
                    .filter { it.severity == HighlightSeverity.ERROR || it.severity == HighlightSeverity.WARNING }
                    .mapNotNull { it.description }
                    .distinct()

                if (errorMessages.isNotEmpty()) {
                    val combinedMessage = errorMessages.joinToString("; ")
                    logger.info("找到 IDE 错误信息: $combinedMessage")
                    return combinedMessage
                }
            }

            // 方法2：使用 PSI 分析获取详细错误
            val psiErrors = getPsiErrorsAtOffset(file, offset)
            if (psiErrors.isNotEmpty()) {
                val combinedMessage = psiErrors.joinToString("; ")
                logger.info("找到 PSI 错误: $combinedMessage")
                return combinedMessage
            }

            // 方法3：检测常见语法问题（作为最后后备）
            val syntaxIssues = detectSyntaxIssuesAtCursor(editor, file, offset)
            if (syntaxIssues.isNotEmpty()) {
                val combinedMessage = syntaxIssues.joinToString("; ")
                logger.info("检测到语法问题: $combinedMessage")
                return combinedMessage
            }

            logger.debug("没有找到具体的错误信息")

        } catch (e: Exception) {
            logger.error("获取错误信息时发生异常", e)
        }

        // 如果没有找到具体错误，返回通用提示
        return "代码可能存在潜在问题，请检查语法、类型、逻辑等方面"
    }

    /**
     * 在指定偏移处获取高亮信息
     * 使用 DaemonCodeAnalyzerEx 获取真正的 IDE 错误信息
     */
    private fun getHighlightInfosAtOffset(file: PsiFile, offset: Int): List<HighlightInfo> {
        val project = file.project
        val daemonAnalyzer = DaemonCodeAnalyzerEx.getInstanceEx(project)
        val document = PsiDocumentManager.getInstance(project).getDocument(file) ?: return emptyList()

        logger.debug("使用 DaemonCodeAnalyzerEx 获取高亮信息")

        // 确保文档已保存和 PSI 已同步
        PsiDocumentManager.getInstance(project).commitDocument(document)

        // 等待分析完成
        ensureAnalysisComplete(daemonAnalyzer, file)

        val result = mutableListOf<HighlightInfo>()

        // 使用较小的范围来提高性能
        val scanRange = TextRange(
            maxOf(0, offset - 50),
            minOf(document.textLength, offset + 50)
        )

        try {
            // 处理所有严重级别
            val severities = arrayOf(
                HighlightSeverity.ERROR,
                HighlightSeverity.WARNING,
                HighlightSeverity.WEAK_WARNING,
                HighlightSeverity.INFORMATION
            )

            for (severity in severities) {
                val processed = DaemonCodeAnalyzerEx.processHighlights(
                    document,
                    project,
                    severity,
                    scanRange.startOffset,
                    scanRange.endOffset,
                    com.intellij.util.Processor<HighlightInfo> { info ->
                        val textRange = TextRange(info.startOffset, info.endOffset)
                        if (textRange.containsOffset(offset)) {
                            result.add(info)
                            logger.debug("找到高亮信息: ${info.description} (严重性: ${info.severity})")
                        }
                        true
                    }
                )

                if (processed && result.isNotEmpty()) {
                    break // 找到错误后就不再继续
                }
            }
        } catch (e: Exception) {
            logger.warn("处理高亮信息时出错", e)
        }

        logger.debug("共找到 ${result.size} 个高亮信息")
        return result
    }

    /**
     * 从 PSI 获取错误信息
     * 包含语法错误和未解析的引用
     */
    private fun getPsiErrorsAtOffset(file: PsiFile, offset: Int): List<String> {
        val errors = mutableListOf<String>()

        try {
            // 获取 PSI 错误元素
            val errorElements = PsiTreeUtil.findChildrenOfType(file, PsiErrorElement::class.java)
            for (errorElement in errorElements) {
                val textRange = errorElement.textRange
                if (textRange != null && textRange.containsOffset(offset)) {
                    val description = errorElement.errorDescription
                    if (description != null && description.isNotBlank()) {
                        errors.add(description)
                        logger.debug("找到 PSI 错误元素: $description")
                    }
                }
            }

            // 检查未解析的引用 - 使用通用的 PsiElement 遍历
            file.accept(object : com.intellij.psi.PsiElementVisitor() {
                override fun visitElement(element: PsiElement) {
                    try {
                        // 获取元素的文本范围
                        val textRange = element.textRange
                        if (textRange != null && textRange.containsOffset(offset)) {
                            // 检查引用
                            val references = element.references
                            for (reference in references) {
                                if (reference.resolve() == null) {
                                    val refText = element.text
                                    if (refText.isNotBlank()) {
                                        errors.add("未定义的变量或类型: $refText")
                                        logger.debug("找到未解析的引用: $refText")
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        // 忽略访问异常
                    }
                    super.visitElement(element)
                }
            })
        } catch (e: Exception) {
            logger.warn("扫描 PSI 错误时出错", e)
        }

        return errors.distinct()
    }

    /**
     * 检测光标位置的语法问题
     * 作为最后的后备方案
     */
    private fun detectSyntaxIssuesAtCursor(editor: Editor, file: PsiFile, offset: Int): List<String> {
        val issues = mutableListOf<String>()

        try {
            val document = editor.document
            val lineNumber = document.getLineNumber(offset)
            val lineStart = document.getLineStartOffset(lineNumber)
            val lineEnd = document.getLineEndOffset(lineNumber)
            val lineText = document.getText(TextRange(lineStart, lineEnd))

            // 使用现有的 detectSyntaxIssues 方法
            val diagnosticIssues = detectSyntaxIssues(lineText, lineNumber + 1, lineStart, lineEnd)
            issues.addAll(diagnosticIssues.map { it.message })

        } catch (e: Exception) {
            logger.warn("检测语法问题时出错", e)
        }

        return issues
    }

    /**
     * 确保分析完成
     * 等待 DaemonCodeAnalyzer 完成错误分析
     */
    private fun ensureAnalysisComplete(daemonAnalyzer: DaemonCodeAnalyzerEx, file: PsiFile) {
        if (!daemonAnalyzer.isErrorAnalyzingFinished(file)) {
            logger.debug("等待代码分析完成...")

            ApplicationManager.getApplication().invokeAndWait {
                try {
                    daemonAnalyzer.restart(file)
                } catch (e: Exception) {
                    logger.warn("重启代码分析时出错", e)
                }
            }

            // 短暂等待，让分析开始
            try {
                Thread.sleep(100)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }
    }

    /**
     * 检测常见的语法问题
     */
    private fun detectSyntaxIssues(
        lineText: String, lineNumber: Int, startOffset: Int, endOffset: Int
    ): List<DiagnosticInfo> {
        val issues = mutableListOf<DiagnosticInfo>()
        val trimmedLine = lineText.trim()

        // 检查常见的语法错误模式
        when {
            // 未闭合的括号
            trimmedLine.count { it == '(' } != trimmedLine.count { it == ')' } -> {
                issues.add(
                    DiagnosticInfo(
                        type = DiagnosticType.ERROR,
                        message = "括号不匹配",
                        startOffset = startOffset,
                        endOffset = endOffset,
                        lineNumber = lineNumber
                    )
                )
            }

            // 未闭合的大括号
            trimmedLine.count { it == '{' } != trimmedLine.count { it == '}' } -> {
                issues.add(
                    DiagnosticInfo(
                        type = DiagnosticType.ERROR,
                        message = "大括号不匹配",
                        startOffset = startOffset,
                        endOffset = endOffset,
                        lineNumber = lineNumber
                    )
                )
            }

            // 行尾缺少分号（对于类似 Java 的语言）
            (trimmedLine.isNotEmpty() && !trimmedLine.endsWith("{") && !trimmedLine.endsWith("}") && !trimmedLine.endsWith(
                ";"
            ) && !trimmedLine.startsWith("//") && !trimmedLine.startsWith("/*") && !trimmedLine.startsWith("*") && !trimmedLine.contains(
                "import "
            ) && !trimmedLine.contains("package ") && !trimmedLine.contains("class ") && !trimmedLine.contains("interface ") && !trimmedLine.contains(
                "enum "
            )) -> {
                issues.add(
                    DiagnosticInfo(
                        type = DiagnosticType.WARNING,
                        message = "可能缺少分号",
                        startOffset = startOffset,
                        endOffset = endOffset,
                        lineNumber = lineNumber
                    )
                )
            }
        }

        return issues
    }


    /**
     * 提取错误范围
     */
    fun extractErrorRange(diagnostic: DiagnosticInfo): TextRange {
        return TextRange(diagnostic.startOffset, diagnostic.endOffset)
    }

    /**
     * 获取问题描述
     */
    fun getProblemDescription(diagnostic: DiagnosticInfo): String {
        return diagnostic.message
    }
}

/**
 * 诊断信息数据类
 */
data class DiagnosticInfo(
    val type: DiagnosticType, val message: String, val startOffset: Int, val endOffset: Int, val lineNumber: Int
)

/**
 * 诊断类型枚举
 */
enum class DiagnosticType {
    ERROR, WARNING, INFO, WEAK_WARNING
}
