package com.freemusic.data.remote.api

import com.freemusic.data.remote.model.*
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 网易云音乐API接口
 * 
 * 基于开源项目 NeteaseCloudMusicApi
 * GitHub: https://github.com/Binaryify/NeteaseCloudMusicApi
 * 
 * 注意：需要自建或使用公共API服务器
 */
interface NeteaseApiService {
    
    /**
     * 搜索歌曲
     * 
     * @param keywords 搜索关键词
     * @param limit 返回数量，默认30
     * @param offset 偏移量，用于分页
     * @param type 搜索类型：1-单曲, 10-专辑, 100-歌手, 1000-歌单
     */
    @GET("search")
    suspend fun searchSongs(
        @Query("keywords") keywords: String,
        @Query("limit") limit: Int = 30,
        @Query("offset") offset: Int = 0,
        @Query("type") type: Int = 1
    ): SearchResponse
    
    /**
     * 获取歌曲播放URL
     * 
     * @param id 歌曲ID
     * @param br 比特率，128000/320000/999000（无损）
     */
    @GET("song/url/v1")
    suspend fun getSongUrl(
        @Query("id") id: Long,
        @Query("level") level: String = "standard"  // standard/higher/exhigh/lossless
    ): SongUrlResponse
    
    /**
     * 获取歌词
     * 
     * @param id 歌曲ID
     */
    @GET("lyric")
    suspend fun getLyric(
        @Query("id") id: Long
    ): LyricResponse
    
    /**
     * 获取推荐歌单
     * 
     * @param limit 返回数量
     */
    @GET("personalized")
    suspend fun getRecommendPlaylists(
        @Query("limit") limit: Int = 10
    ): PlaylistRecommendResponse
    
    /**
     * 获取歌单详情
     * 
     * @param id 歌单ID
     */
    @GET("playlist/detail")
    suspend fun getPlaylistDetail(
        @Query("id") id: Long
    ): PlaylistDetailResponse
    
    /**
     * 获取热搜列表
     */
    @GET("search/hot/detail")
    suspend fun getHotSearch(): HotSearchResponse
    
    /**
     * 获取每日推荐歌曲
     * 需要登录
     */
    @GET("recommend/songs")
    suspend fun getDailyRecommend(): DailyRecommendResponse
}
