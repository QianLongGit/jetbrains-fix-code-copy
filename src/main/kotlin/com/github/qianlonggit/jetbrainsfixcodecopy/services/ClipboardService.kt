package com.github.qianlonggit.jetbrainsfixcodecopy.services

import com.github.qianlonggit.jetbrainsfixcodecopy.MyBundle
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.components.Service
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.project.Project
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection

/**
 * 剪贴板服务
 *
 * 负责将生成的 Prompt 复制到系统剪贴板
 *
 * @author qianlonggit
 * @since 2025-12-13
 */
@Service(Service.Level.APP)
class ClipboardService {

    private val logger = com.intellij.openapi.diagnostic.Logger.getInstance(ClipboardService::class.java)

    companion object {
        /**
         * 获取服务实例
         */
        fun getInstance(): ClipboardService {
            return com.intellij.openapi.components.ServiceManager.getService(ClipboardService::class.java)
        }

        /**
         * 复制内容到剪贴板（静态方法）
         *
         * @param content 要复制的内容
         * @param project 项目实例（用于显示通知）
         */
        fun copyToClipboard(content: String, project: Project? = null) {
            // 使用 CopyPasteManager 复制到系统剪贴板
            CopyPasteManager.getInstance().setContents(StringSelection(content))

            // 显示成功通知
            showSuccessNotification(project)

            // 记录日志
            logCopyAction(content, project)
        }

        /**
         * 显示成功通知
         */
        private fun showSuccessNotification(project: Project?) {
            val notification = Notification(
                "Copy Fix Prompt",
                MyBundle.message("copy.success.title"),
                MyBundle.message("copy.success.message"),
                NotificationType.INFORMATION
            )

            if (project != null) {
                Notifications.Bus.notify(notification, project)
            } else {
                Notifications.Bus.notify(notification)
            }
        }

        /**
         * 记录日志
         */
        private fun logCopyAction(content: String, project: Project?) {
            val logger = com.intellij.openapi.diagnostic.Logger.getInstance(ClipboardService::class.java)

            // 记录复制操作（不记录具体内容，保护隐私）
            logger.info("Copied fix prompt to clipboard. Length: ${content.length} chars")
        }
    }

    /**
     * 复制内容到剪贴板
     *
     * @param content 要复制的内容
     * @param project 项目实例
     */
    fun copy(content: String, project: Project? = null) {
        try {
            // 验证输入
            if (content.isBlank()) {
                showWarningNotification(project, "内容为空，未复制到剪贴板")
                return
            }

            // 执行复制
            copyToClipboard(content, project)

        } catch (e: Exception) {
            // 处理异常
            logger.error("Failed to copy to clipboard", e)
            showErrorNotification(project, e.message ?: "未知错误")
        }
    }

    /**
     * 获取剪贴板内容
     */
    fun getClipboardContent(): String? {
        return try {
            val contents = CopyPasteManager.getInstance().getContents()
            if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                contents.getTransferData(DataFlavor.stringFlavor) as? String
            } else {
                null
            }
        } catch (e: Exception) {
            logger.error("Failed to get clipboard content", e)
            null
        }
    }

    /**
     * 显示错误通知
     */
    private fun showErrorNotification(project: Project?, message: String) {
        val notification = Notification(
            "Copy Fix Prompt Error",
            "复制失败",
            "无法复制到剪贴板: $message",
            NotificationType.ERROR
        )

        if (project != null) {
            Notifications.Bus.notify(notification, project)
        } else {
            Notifications.Bus.notify(notification)
        }
    }

    /**
     * 显示警告通知
     */
    private fun showWarningNotification(project: Project?, message: String) {
        val notification = Notification(
            "Copy Fix Prompt Warning",
            "复制警告",
            message,
            NotificationType.WARNING
        )

        if (project != null) {
            Notifications.Bus.notify(notification, project)
        } else {
            Notifications.Bus.notify(notification)
        }
    }
}
