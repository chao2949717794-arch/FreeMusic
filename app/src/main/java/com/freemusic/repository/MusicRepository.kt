package com.freemusic.repository

import com.freemusic.data.local.database.AppDatabase
import com.freemusic.data.local.database.entity.FavoriteSongEntity
import com.freemusic.data.local.database.entity.PlayHistoryEntity
import com.freemusic.data.mapper.*
import com.freemusic.data.remote.RetrofitClient
import com.freemusic.model.Playlist
import com.freemusic.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 音乐数据仓库
 * 
 * 负责协调本地数据库和远程API，提供统一的数据访问接口
 * 实现数据缓存、离线支持等功能
 */
class MusicRepository(private val database: AppDatabase) {
    
    private val api = RetrofitClient.neteaseApi
    private val songDao = database.songDao()
    private val playHistoryDao = database.playHistoryDao()
    private val favoriteSongDao = database.favoriteSongDao()
    
    /**
     * 搜索歌曲
     * 
     * @param keywords 搜索关键词
     * @return 搜索结果列表
     */
    suspend fun searchSongs(keywords: String): Result<List<Song>> {
        return try {
            val response = api.searchSongs(keywords)
            if (response.code == 200 && response.result != null) {
                val songs = response.result.songs?.map { it.toDomainModel() } ?: emptyList()
                
                // 缓存到本地数据库
                val entities = songs.map { it.toEntity() }
                songDao.insertSongs(entities)
                
                Result.success(songs)
            } else {
                Result.failure(Exception("搜索失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取歌曲播放URL
     * 
     * @param songId 歌曲ID
     * @return 播放URL
     */
    suspend fun getSongUrl(songId: Long): Result<String> {
        return try {
            val response = api.getSongUrl(songId)
            if (response.code == 200 && !response.data.isNullOrEmpty()) {
                val url = response.data.first().url
                if (!url.isNullOrEmpty()) {
                    Result.success(url)
                } else {
                    Result.failure(Exception("无法获取播放链接"))
                }
            } else {
                Result.failure(Exception("获取播放链接失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取歌词
     * 
     * @param songId 歌曲ID
     * @return 歌词文本
     */
    suspend fun getLyric(songId: Long): Result<String> {
        return try {
            val response = api.getLyric(songId)
            if (response.code == 200 && response.lyric != null) {
                Result.success(response.lyric.lyric)
            } else {
                Result.failure(Exception("获取歌词失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取推荐歌单
     */
    suspend fun getRecommendPlaylists(): Result<List<Playlist>> {
        return try {
            val response = api.getRecommendPlaylists()
            if (response.code == 200 && !response.result.isNullOrEmpty()) {
                val playlists = response.result.map { it.toDomainModel() }
                Result.success(playlists)
            } else {
                Result.failure(Exception("获取推荐歌单失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 获取歌单详情
     */
    suspend fun getPlaylistDetail(playlistId: Long): Result<Playlist> {
        return try {
            val response = api.getPlaylistDetail(playlistId)
            if (response.code == 200 && response.playlist != null) {
                val playlist = response.playlist.toDomainModel()
                Result.success(playlist)
            } else {
                Result.failure(Exception("获取歌单详情失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 添加播放历史
     */
    suspend fun addPlayHistory(song: Song) {
        val history = PlayHistoryEntity(
            songId = song.id,
            songName = song.name,
            artist = song.artist,
            coverUrl = song.coverUrl
        )
        playHistoryDao.insertPlayHistory(history)
        
        // 清理旧历史（保留最近500条）
        playHistoryDao.deleteOldHistory()
    }
    
    /**
     * 获取播放历史
     */
    fun getPlayHistory(): Flow<List<Song>> {
        return playHistoryDao.getPlayHistory().map { histories ->
            histories.map { it.toDomainModel() }
        }
    }
    
    /**
     * 添加收藏
     */
    suspend fun addFavorite(song: Song) {
        val favorite = FavoriteSongEntity(
            songId = song.id,
            songName = song.name,
            artist = song.artist,
            album = song.album,
            coverUrl = song.coverUrl,
            duration = song.duration
        )
        favoriteSongDao.insertFavoriteSong(favorite)
    }
    
    /**
     * 移除收藏
     */
    suspend fun removeFavorite(songId: Long) {
        favoriteSongDao.deleteFavoriteSong(songId)
    }
    
    /**
     * 检查是否收藏
     */
    suspend fun isFavorite(songId: Long): Boolean {
        return favoriteSongDao.isFavorite(songId)
    }
    
    /**
     * 获取所有收藏歌曲
     */
    fun getFavoriteSongs(): Flow<List<Song>> {
        return favoriteSongDao.getAllFavoriteSongs().map { favorites ->
            favorites.map { it.toDomainModel() }
        }
    }
}
