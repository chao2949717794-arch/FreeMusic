package com.freemusic.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.freemusic.data.local.database.AppDatabase
import com.freemusic.model.PlaybackState
import com.freemusic.model.Song
import com.freemusic.player.MusicPlayer
import com.freemusic.repository.MusicRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 主ViewModel
 * 管理播放器状态、音乐数据和UI状态
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    // 播放器实例
    private val musicPlayer = MusicPlayer(application)
    
    // 数据仓库
    private val database = AppDatabase.getDatabase(application)
    private val repository = MusicRepository(database)
    
    // 播放状态
    val playbackState: StateFlow<PlaybackState> = musicPlayer.playbackState
    
    // 当前播放位置
    val currentPosition: StateFlow<Long> = musicPlayer.currentPosition
    
    // 播放队列
    val playlist: StateFlow<List<Song>> = musicPlayer.playlist
    
    // 当前索引
    val currentIndex: StateFlow<Int> = musicPlayer.currentIndex
    
    // 播放模式
    val playMode = musicPlayer.playMode
    
    // 收藏歌曲列表
    val favoriteSongs: StateFlow<List<Song>> = repository.getFavoriteSongs()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    // 播放历史
    val playHistory: StateFlow<List<Song>> = repository.getPlayHistory()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    // 搜索结果
    private val _searchResults = MutableStateFlow<List<Song>>(emptyList())
    val searchResults: StateFlow<List<Song>> = _searchResults.asStateFlow()
    
    // 推荐歌单
    private val _recommendPlaylists = MutableStateFlow<List<com.freemusic.model.Playlist>>(emptyList())
    val recommendPlaylists: StateFlow<List<com.freemusic.model.Playlist>> = _recommendPlaylists.asStateFlow()
    
    // 加载状态
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // 错误消息
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        // 加载推荐歌单
        loadRecommendPlaylists()
    }
    
    /**
     * 搜索歌曲
     */
    fun searchSongs(keywords: String) {
        if (keywords.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            repository.searchSongs(keywords)
                .onSuccess { songs ->
                    _searchResults.value = songs
                }
                .onFailure { e ->
                    _errorMessage.value = "搜索失败：${e.message}"
                }
            
            _isLoading.value = false
        }
    }
    
    /**
     * 播放歌曲
     * 自动获取播放URL并添加到播放历史
     */
    fun playSong(song: Song) {
        viewModelScope.launch {
            _isLoading.value = true
            
            // 获取播放URL
            repository.getSongUrl(song.id)
                .onSuccess { url ->
                    val songWithUrl = song.copy(url = url)
                    musicPlayer.play(songWithUrl)
                    
                    // 添加播放历史
                    repository.addPlayHistory(songWithUrl)
                    
                    // 获取歌词
                    loadLyric(song.id)
                }
                .onFailure { e ->
                    _errorMessage.value = "播放失败：${e.message}"
                }
            
            _isLoading.value = false
        }
    }
    
    /**
     * 播放歌单
     */
    fun playList(songs: List<Song>, startIndex: Int = 0) {
        viewModelScope.launch {
            _isLoading.value = true
            
            // 批量获取播放URL（这里简化处理，实际应该批量请求）
            val songsWithUrl = songs.map { song ->
                repository.getSongUrl(song.id)
                    .getOrNull()
                    ?.let { url -> song.copy(url = url) }
                    ?: song
            }
            
            musicPlayer.playList(songsWithUrl, startIndex)
            _isLoading.value = false
        }
    }
    
    /**
     * 暂停/继续播放
     */
    fun togglePlayPause() {
        if (musicPlayer.isPlaying()) {
            musicPlayer.pause()
        } else {
            musicPlayer.resume()
        }
    }
    
    /**
     * 下一曲
     */
    fun next() {
        musicPlayer.next()
    }
    
    /**
     * 上一曲
     */
    fun previous() {
        musicPlayer.previous()
    }
    
    /**
     * 跳转到指定位置
     */
    fun seekTo(position: Long) {
        musicPlayer.seekTo(position)
    }
    
    /**
     * 切换播放模式
     */
    fun togglePlayMode() {
        musicPlayer.togglePlayMode()
    }
    
    /**
     * 切换收藏状态
     */
    fun toggleFavorite(song: Song) {
        viewModelScope.launch {
            val isFav = repository.isFavorite(song.id)
            if (isFav) {
                repository.removeFavorite(song.id)
            } else {
                repository.addFavorite(song)
            }
        }
    }
    
    /**
     * 加载推荐歌单
     */
    private fun loadRecommendPlaylists() {
        viewModelScope.launch {
            repository.getRecommendPlaylists()
                .onSuccess { playlists ->
                    _recommendPlaylists.value = playlists
                }
                .onFailure { e ->
                    // 静默失败，不影响用户体验
                }
        }
    }
    
    /**
     * 加载歌词
     */
    private fun loadLyric(songId: Long) {
        viewModelScope.launch {
            repository.getLyric(songId)
                .onSuccess { lyric ->
                    // 更新当前歌曲的歌词
                    val currentSong = musicPlayer.getCurrentSong()
                    currentSong?.lyric = lyric
                }
        }
    }
    
    /**
     * 清除错误消息
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    override fun onCleared() {
        super.onCleared()
        musicPlayer.release()
    }
}
