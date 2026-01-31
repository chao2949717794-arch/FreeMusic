package com.freemusic.player

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.freemusic.model.PlayMode
import com.freemusic.model.PlaybackState
import com.freemusic.model.Song
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 音乐播放器核心类
 * 
 * 封装ExoPlayer和MediaController，提供统一的播放控制接口
 * 支持播放队列管理、播放模式切换、状态监听等功能
 * 
 * @param context 上下文
 */
class MusicPlayer(private val context: Context) {
    
    // MediaController Future对象
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null
    
    // 播放状态
    private val _playbackState = MutableStateFlow<PlaybackState>(PlaybackState.Idle)
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()
    
    // 当前播放位置（毫秒）
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()
    
    // 播放队列
    private val _playlist = MutableStateFlow<List<Song>>(emptyList())
    val playlist: StateFlow<List<Song>> = _playlist.asStateFlow()
    
    // 当前播放索引
    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()
    
    // 播放模式
    private val _playMode = MutableStateFlow(PlayMode.SEQUENTIAL)
    val playMode: StateFlow<PlayMode> = _playMode.asStateFlow()
    
    init {
        initializeController()
    }
    
    /**
     * 初始化MediaController
     * 连接到MusicService
     */
    private fun initializeController() {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, MusicService::class.java)
        )
        
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener(
            {
                mediaController = controllerFuture?.get()
                setupPlayerListener()
            },
            MoreExecutors.directExecutor()
        )
    }
    
    /**
     * 设置播放器监听器
     */
    private fun setupPlayerListener() {
        mediaController?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                updatePlaybackState()
            }
            
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updatePlaybackState()
            }
            
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                // 歌曲切换
                val index = mediaController?.currentMediaItemIndex ?: 0
                _currentIndex.value = index
                updatePlaybackState()
            }
        })
    }
    
    /**
     * 更新播放状态
     */
    private fun updatePlaybackState() {
        val controller = mediaController ?: return
        val currentSong = getCurrentSong()
        
        _playbackState.value = when {
            controller.playbackState == Player.STATE_IDLE -> PlaybackState.Idle
            controller.playbackState == Player.STATE_BUFFERING -> PlaybackState.Loading
            controller.isPlaying && currentSong != null -> PlaybackState.Playing(currentSong)
            !controller.isPlaying && currentSong != null -> PlaybackState.Paused(currentSong)
            else -> PlaybackState.Idle
        }
        
        // 更新播放位置
        _currentPosition.value = controller.currentPosition
    }
    
    /**
     * 播放单曲
     * 
     * @param song 要播放的歌曲
     */
    fun play(song: Song) {
        _playlist.value = listOf(song)
        _currentIndex.value = 0
        playAtIndex(0)
    }
    
    /**
     * 播放歌单
     * 
     * @param songs 歌曲列表
     * @param startIndex 起始索引
     */
    fun playList(songs: List<Song>, startIndex: Int = 0) {
        _playlist.value = songs
        _currentIndex.value = startIndex
        
        // 转换为MediaItem列表
        val mediaItems = songs.map { song ->
            MediaItem.Builder()
                .setMediaId(song.id.toString())
                .setUri(song.url)
                .build()
        }
        
        mediaController?.apply {
            setMediaItems(mediaItems, startIndex, 0)
            prepare()
            play()
        }
    }
    
    /**
     * 播放指定索引的歌曲
     */
    private fun playAtIndex(index: Int) {
        if (index < 0 || index >= _playlist.value.size) return
        
        val song = _playlist.value[index]
        val mediaItem = MediaItem.Builder()
            .setMediaId(song.id.toString())
            .setUri(song.url)
            .build()
        
        mediaController?.apply {
            setMediaItem(mediaItem)
            prepare()
            play()
        }
    }
    
    /**
     * 暂停播放
     */
    fun pause() {
        mediaController?.pause()
    }
    
    /**
     * 继续播放
     */
    fun resume() {
        mediaController?.play()
    }
    
    /**
     * 下一曲
     */
    fun next() {
        when (_playMode.value) {
            PlayMode.SEQUENTIAL -> {
                val nextIndex = (_currentIndex.value + 1) % _playlist.value.size
                _currentIndex.value = nextIndex
                mediaController?.seekToNext()
            }
            PlayMode.SHUFFLE -> {
                val randomIndex = (0 until _playlist.value.size).random()
                _currentIndex.value = randomIndex
                mediaController?.seekToDefaultPosition(randomIndex)
                mediaController?.play()
            }
            PlayMode.REPEAT_ONE -> {
                // 单曲循环，重新播放当前歌曲
                mediaController?.seekTo(0)
                mediaController?.play()
            }
        }
    }
    
    /**
     * 上一曲
     */
    fun previous() {
        val prevIndex = if (_currentIndex.value > 0) {
            _currentIndex.value - 1
        } else {
            _playlist.value.size - 1
        }
        _currentIndex.value = prevIndex
        mediaController?.seekToPrevious()
    }
    
    /**
     * 跳转到指定位置
     * 
     * @param position 位置（毫秒）
     */
    fun seekTo(position: Long) {
        mediaController?.seekTo(position)
        _currentPosition.value = position
    }
    
    /**
     * 切换播放模式
     */
    fun togglePlayMode() {
        _playMode.value = when (_playMode.value) {
            PlayMode.SEQUENTIAL -> PlayMode.SHUFFLE
            PlayMode.SHUFFLE -> PlayMode.REPEAT_ONE
            PlayMode.REPEAT_ONE -> PlayMode.SEQUENTIAL
        }
        
        // 设置ExoPlayer的循环模式
        mediaController?.repeatMode = when (_playMode.value) {
            PlayMode.REPEAT_ONE -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_OFF
        }
    }
    
    /**
     * 获取当前播放的歌曲
     */
    fun getCurrentSong(): Song? {
        return if (_currentIndex.value in _playlist.value.indices) {
            _playlist.value[_currentIndex.value]
        } else null
    }
    
    /**
     * 检查是否正在播放
     */
    fun isPlaying(): Boolean {
        return mediaController?.isPlaying ?: false
    }
    
    /**
     * 释放资源
     */
    fun release() {
        mediaController?.release()
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
    }
}
