package com.freemusic.data.mapper

import com.freemusic.data.local.database.entity.*
import com.freemusic.data.remote.model.*
import com.freemusic.model.Song
import com.freemusic.model.Playlist

/**
 * 数据映射器
 * 负责在不同数据层之间转换数据模型
 */

/**
 * 将网络DTO转换为领域模型
 */
fun SongDto.toDomainModel(): Song {
    return Song(
        id = this.id,
        name = this.name,
        artist = this.artists.joinToString(", ") { it.name },
        album = this.album.name,
        coverUrl = this.album.coverUrl,
        duration = this.duration,
        source = "netease"
    )
}

/**
 * 将领域模型转换为数据库实体
 */
fun Song.toEntity(): SongEntity {
    return SongEntity(
        id = this.id,
        name = this.name,
        artist = this.artist,
        album = this.album,
        coverUrl = this.coverUrl,
        duration = this.duration,
        source = this.source,
        url = this.url
    )
}

/**
 * 将数据库实体转换为领域模型
 */
fun SongEntity.toDomainModel(): Song {
    return Song(
        id = this.id,
        name = this.name,
        artist = this.artist,
        album = this.album,
        coverUrl = this.coverUrl,
        duration = this.duration,
        source = this.source,
        url = this.url
    )
}

/**
 * 将收藏实体转换为领域模型
 */
fun FavoriteSongEntity.toDomainModel(): Song {
    return Song(
        id = this.songId,
        name = this.songName,
        artist = this.artist,
        album = this.album,
        coverUrl = this.coverUrl,
        duration = this.duration
    )
}

/**
 * 将歌单DTO转换为领域模型
 */
fun PlaylistDto.toDomainModel(): Playlist {
    return Playlist(
        id = this.id,
        name = this.name,
        coverUrl = this.coverUrl
    )
}

/**
 * 将歌单详情DTO转换为领域模型
 */
fun PlaylistDetailDto.toDomainModel(): Playlist {
    return Playlist(
        id = this.id,
        name = this.name,
        description = this.description ?: "",
        coverUrl = this.coverUrl,
        songs = this.tracks?.map { it.toDomainModel() } ?: emptyList()
    )
}

/**
 * 将播放历史实体转换为领域模型
 */
fun PlayHistoryEntity.toDomainModel(): Song {
    return Song(
        id = this.songId,
        name = this.songName,
        artist = this.artist,
        coverUrl = this.coverUrl
    )
}
