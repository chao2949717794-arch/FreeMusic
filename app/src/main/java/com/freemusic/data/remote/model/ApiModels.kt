package com.freemusic.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * API响应基类
 */
data class BaseResponse(
    val code: Int,
    val message: String? = null
)

/**
 * 搜索响应
 */
data class SearchResponse(
    val code: Int,
    val result: SearchResult? = null
)

data class SearchResult(
    val songs: List<SongDto>? = emptyList(),
    val songCount: Int = 0
)

/**
 * 歌曲DTO（数据传输对象）
 */
data class SongDto(
    val id: Long,
    val name: String,
    @SerializedName("ar") val artists: List<ArtistDto>,
    @SerializedName("al") val album: AlbumDto,
    @SerializedName("dt") val duration: Long  // 毫秒
)

data class ArtistDto(
    val id: Long,
    val name: String
)

data class AlbumDto(
    val id: Long,
    val name: String,
    @SerializedName("picUrl") val coverUrl: String
)

/**
 * 歌曲URL响应
 */
data class SongUrlResponse(
    val code: Int,
    val data: List<SongUrlData>? = emptyList()
)

data class SongUrlData(
    val id: Long,
    val url: String?,
    val br: Int,  // 比特率
    val size: Long,  // 文件大小
    val type: String?  // 文件类型
)

/**
 * 歌词响应
 */
data class LyricResponse(
    val code: Int,
    @SerializedName("lrc") val lyric: LyricData?,
    @SerializedName("tlyric") val translatedLyric: LyricData?  // 翻译歌词
)

data class LyricData(
    val lyric: String
)

/**
 * 歌单推荐响应
 */
data class PlaylistRecommendResponse(
    val code: Int,
    val result: List<PlaylistDto>? = emptyList()
)

data class PlaylistDto(
    val id: Long,
    val name: String,
    @SerializedName("picUrl") val coverUrl: String,
    val playCount: Long,
    val trackCount: Int
)

/**
 * 歌单详情响应
 */
data class PlaylistDetailResponse(
    val code: Int,
    val playlist: PlaylistDetailDto?
)

data class PlaylistDetailDto(
    val id: Long,
    val name: String,
    val description: String?,
    @SerializedName("coverImgUrl") val coverUrl: String,
    val tracks: List<SongDto>? = emptyList()
)

/**
 * 热搜响应
 */
data class HotSearchResponse(
    val code: Int,
    val data: List<HotSearchItem>? = emptyList()
)

data class HotSearchItem(
    val searchWord: String,
    val score: Long,
    val content: String,
    val iconUrl: String?
)

/**
 * 每日推荐响应
 */
data class DailyRecommendResponse(
    val code: Int,
    val data: DailyRecommendData?
)

data class DailyRecommendData(
    val dailySongs: List<SongDto>? = emptyList()
)
