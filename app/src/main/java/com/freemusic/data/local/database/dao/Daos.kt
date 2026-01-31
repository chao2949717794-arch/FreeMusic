package com.freemusic.data.local.database.dao

import androidx.room.*
import com.freemusic.data.local.database.entity.*
import kotlinx.coroutines.flow.Flow

/**
 * 歌曲DAO
 */
@Dao
interface SongDao {
    @Query("SELECT * FROM songs WHERE id = :id")
    suspend fun getSongById(id: Long): SongEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: SongEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<SongEntity>)
    
    @Query("DELETE FROM songs WHERE updateTime < :expiryTime")
    suspend fun deleteExpiredSongs(expiryTime: Long)
}

/**
 * 播放历史DAO
 */
@Dao
interface PlayHistoryDao {
    @Query("SELECT * FROM play_history ORDER BY playTime DESC LIMIT :limit")
    fun getPlayHistory(limit: Int = 100): Flow<List<PlayHistoryEntity>>
    
    @Insert
    suspend fun insertPlayHistory(history: PlayHistoryEntity)
    
    @Query("DELETE FROM play_history WHERE id NOT IN (SELECT id FROM play_history ORDER BY playTime DESC LIMIT 500)")
    suspend fun deleteOldHistory()
    
    @Query("DELETE FROM play_history")
    suspend fun clearAll()
}

/**
 * 收藏歌曲DAO
 */
@Dao
interface FavoriteSongDao {
    @Query("SELECT * FROM favorite_songs ORDER BY addTime DESC")
    fun getAllFavoriteSongs(): Flow<List<FavoriteSongEntity>>
    
    @Query("SELECT * FROM favorite_songs WHERE songId = :songId")
    suspend fun getFavoriteSong(songId: Long): FavoriteSongEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteSong(song: FavoriteSongEntity)
    
    @Query("DELETE FROM favorite_songs WHERE songId = :songId")
    suspend fun deleteFavoriteSong(songId: Long)
    
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_songs WHERE songId = :songId)")
    suspend fun isFavorite(songId: Long): Boolean
}

/**
 * 歌单DAO
 */
@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY updateTime DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>
    
    @Query("SELECT * FROM playlists WHERE id = :id")
    suspend fun getPlaylistById(id: Long): PlaylistEntity?
    
    @Insert
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long
    
    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)
    
    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)
}

/**
 * 歌单-歌曲关系DAO
 */
@Dao
interface PlaylistSongDao {
    @Query("SELECT * FROM playlist_songs WHERE playlistId = :playlistId ORDER BY orderIndex")
    fun getPlaylistSongs(playlistId: Long): Flow<List<PlaylistSongEntity>>
    
    @Insert
    suspend fun insertPlaylistSong(playlistSong: PlaylistSongEntity)
    
    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun deletePlaylistSong(playlistId: Long, songId: Long)
    
    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId")
    suspend fun deleteAllPlaylistSongs(playlistId: Long)
}
