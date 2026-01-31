package com.freemusic

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

/**
 * 应用程序入口类
 * 负责初始化全局配置和服务
 */
class FreeMusicApplication : Application() {
    
    companion object {
        // 通知渠道ID
        const val NOTIFICATION_CHANNEL_ID = "music_playback_channel"
        const val NOTIFICATION_CHANNEL_NAME = "音乐播放"
    }
    
    override fun onCreate() {
        super.onCreate()
        
        // 创建通知渠道（Android 8.0+）
        createNotificationChannel()
    }
    
    /**
     * 创建音乐播放通知渠道
     * 用于显示播放器控制通知
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW  // 低优先级，不打扰用户
            ).apply {
                description = "显示正在播放的音乐信息和控制按钮"
                setShowBadge(false)  // 不显示应用角标
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
