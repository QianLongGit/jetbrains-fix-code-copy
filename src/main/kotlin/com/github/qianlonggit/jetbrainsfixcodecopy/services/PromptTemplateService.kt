package com.github.qianlonggit.jetbrainsfixcodecopy.services

import com.github.qianlonggit.jetbrainsfixcodecopy.templates.DefaultPromptTemplate
import com.github.qianlonggit.jetbrainsfixcodecopy.utils.FixContext
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project

/**
 * Prompt 模板服务
 *
 * 负责根据上下文信息生成修复 Prompt
 *
 * @author qianlonggit
 * @since 2025-12-13
 */
@Service(Service.Level.PROJECT)
class PromptTemplateService {

    companion object {
        /**
         * 获取服务实例
         */
        fun getInstance(project: Project): PromptTemplateService {
            return project.getService(PromptTemplateService::class.java)
        }

        /**
         * 生成修复 Prompt（静态方法，用于不依赖 Project 的情况）
         */
        fun generatePrompt(context: FixContext): String {
            return DefaultPromptTemplate.render(context)
        }
    }

    /**
     * 生成修复 Prompt
     *
     * @param context 修复上下文信息
     * @return 格式化的修复 Prompt
     */
    fun generatePrompt(context: FixContext): String {
        return DefaultPromptTemplate.render(context)
    }

    /**
     * 生成自定义模板的 Prompt
     *
     * @param context 修复上下文信息
     * @param template 自定义模板
     * @return 格式化的修复 Prompt
     */
    fun generateCustomPrompt(context: FixContext, template: String): String {
        // 可以在这里实现自定义模板的逻辑
        return renderTemplate(template, context)
    }

    /**
     * 渲染模板
     */
    private fun renderTemplate(template: String, context: FixContext): String {
        return template
            .replace("\${filePath}", context.filePath)
            .replace("\${startLine}", context.startLine.toString())
            .replace("\${endLine}", context.endLine.toString())
            .replace("\${diagnosticText}", context.diagnosticText)
            .replace("\${selectedText}", context.selectedText)
            .replace("\${userInput}", context.userInput)
    }
}
