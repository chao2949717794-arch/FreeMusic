package com.freemusic.player

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.freemusic.MainActivity

/**
 * 音乐播放服务
 * 
 * 继承自MediaSessionService，支持：
 * - 后台播放
 * - 通知栏控制
 * - 锁屏控制
 * - 蓝牙耳机控制
 * - Android Auto车载系统
 */
class MusicService : MediaSessionService() {
    
    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化ExoPlayer
        player = ExoPlayer.Builder(this)
            .build()
            .also {
                // 配置播放器监听器
                it.addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        // 处理播放状态变化
                        when (playbackState) {
                            Player.STATE_ENDED -> {
                                // 播放结束，播放下一曲
                                it.seekToNext()
                            }
                        }
                    }
                })
            }
        
        // 创建MediaSession
        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(getSingleTopActivity())
            .build()
    }
    
    /**
     * 返回MediaSession
     * 系统通过此方法获取会话以显示媒体控制
     */
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }
    
    /**
     * 创建打开应用的PendingIntent
     */
    private fun getSingleTopActivity(): PendingIntent {
        return PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
    
    override fun onDestroy() {
        // 释放资源
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
