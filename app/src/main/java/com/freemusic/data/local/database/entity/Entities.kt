package com.freemusic.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 歌曲实体
 * 缓存从网络获取的歌曲信息
 */
@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val artist: String,
    val album: String,
    val coverUrl: String,
    val duration: Long,  // 毫秒
    val source: String,  // netease/qq/bilibili
    val url: String = "",  // 播放链接
    val updateTime: Long = System.currentTimeMillis()  // 更新时间
)

/**
 * 播放历史实体
 */
@Entity(tableName = "play_history")
data class PlayHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val songId: Long,
    val songName: String,
    val artist: String,
    val coverUrl: String,
    val playTime: Long = System.currentTimeMillis(),  // 播放时间
    val playDuration: Long = 0  // 播放时长（毫秒）
)

/**
 * 收藏歌曲实体
 */
@Entity(tableName = "favorite_songs")
data class FavoriteSongEntity(
    @PrimaryKey val songId: Long,
    val songName: String,
    val artist: String,
    val album: String,
    val coverUrl: String,
    val duration: Long,
    val addTime: Long = System.currentTimeMillis()  // 添加时间
)

/**
 * 歌单实体
 */
@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val coverUrl: String = "",
    val createTime: Long = System.currentTimeMillis(),
    val updateTime: Long = System.currentTimeMillis()
)

/**
 * 歌单-歌曲关系实体
 */
@Entity(tableName = "playlist_songs")
data class PlaylistSongEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val playlistId: Long,
    val songId: Long,
    val songName: String,
    val artist: String,
    val coverUrl: String,
    val orderIndex: Int,  // 排序索引
    val addTime: Long = System.currentTimeMillis()
)
