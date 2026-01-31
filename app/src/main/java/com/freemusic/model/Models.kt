package com.freemusic.model

/**
 * 歌曲数据模型
 * 
 * @property id 歌曲唯一ID
 * @property name 歌曲名称
 * @property artist 艺术家
 * @property album 专辑名称
 * @property coverUrl 封面图片URL
 * @property duration 时长（毫秒）
 * @property source 音乐来源（netease/qq/bilibili）
 * @property url 播放链接
 * @property lyric 歌词内容
 */
data class Song(
    val id: Long,
    val name: String,
    val artist: String,
    val album: String = "",
    val coverUrl: String = "",
    val duration: Long = 0L,  // 毫秒
    val source: String = "netease",
    var url: String = "",  // 播放链接需要动态获取
    var lyric: String = ""  // 歌词
) {
    /**
     * 获取格式化的时长
     * 例如：03:45
     */
    fun getFormattedDuration(): String {
        val minutes = (duration / 1000) / 60
        val seconds = (duration / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}

/**
 * 歌单数据模型
 * 
 * @property id 歌单ID
 * @property name 歌单名称
 * @property description 歌单描述
 * @property coverUrl 封面图片
 * @property songs 歌单中的歌曲列表
 */
data class Playlist(
    val id: Long,
    val name: String,
    val description: String = "",
    val coverUrl: String = "",
    val songs: List<Song> = emptyList()
)

/**
 * 播放状态
 */
sealed class PlaybackState {
    object Idle : PlaybackState()                              // 空闲
    object Loading : PlaybackState()                           // 加载中
    data class Playing(val song: Song) : PlaybackState()       // 播放中
    data class Paused(val song: Song) : PlaybackState()        // 已暂停
    data class Error(val message: String) : PlaybackState()    // 错误
}

/**
 * 播放模式
 */
enum class PlayMode {
    SEQUENTIAL,  // 顺序播放
    SHUFFLE,     // 随机播放
    REPEAT_ONE   // 单曲循环
}
