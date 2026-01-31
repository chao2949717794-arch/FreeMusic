package com.freemusic.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.freemusic.data.local.database.dao.*
import com.freemusic.data.local.database.entity.*

/**
 * Room数据库
 * 
 * 版本：1
 * 包含的表：
 * - SongEntity：歌曲信息
 * - PlayHistoryEntity：播放历史
 * - FavoriteSongEntity：收藏的歌曲
 * - PlaylistEntity：歌单
 * - PlaylistSongEntity：歌单-歌曲关系
 */
@Database(
    entities = [
        SongEntity::class,
        PlayHistoryEntity::class,
        FavoriteSongEntity::class,
        PlaylistEntity::class,
        PlaylistSongEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    // DAO接口
    abstract fun songDao(): SongDao
    abstract fun playHistoryDao(): PlayHistoryDao
    abstract fun favoriteSongDao(): FavoriteSongDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistSongDao(): PlaylistSongDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        /**
         * 获取数据库实例（单例模式）
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "free_music_database"
                )
                    .fallbackToDestructiveMigration()  // 版本升级时销毁重建
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
