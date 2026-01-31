package com.freemusic.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import coil.compose.AsyncImage
import com.freemusic.model.PlayMode
import com.freemusic.model.PlaybackState
import com.freemusic.viewmodel.MainViewModel

/**
 * 播放器界面
 * TODO: 完整实现播放控制和歌词显示
 */
@Composable
fun PlayerScreen(viewModel: MainViewModel) {
    val playbackState by viewModel.playbackState.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    val playMode by viewModel.playMode.collectAsState()
    
    val currentSong = when (val state = playbackState) {
        is PlaybackState.Playing -> state.song
        is PlaybackState.Paused -> state.song
        else -> null
    }
    
    if (currentSong == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("当前没有播放音乐")
        }
        return
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 返回按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = "返回"
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 封面
        AsyncImage(
            model = currentSong.coverUrl,
            contentDescription = currentSong.name,
            modifier = Modifier
                .size(280.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 歌曲信息
        Text(
            text = currentSong.name,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = currentSong.artist,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // 进度条
        Slider(
            value = currentPosition.toFloat(),
            onValueChange = { viewModel.seekTo(it.toLong()) },
            valueRange = 0f..currentSong.duration.toFloat()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(currentPosition),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = currentSong.getFormattedDuration(),
                style = MaterialTheme.typography.bodySmall
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // 控制按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 播放模式
            IconButton(onClick = { viewModel.togglePlayMode() }) {
                Icon(
                    when (playMode) {
                        PlayMode.SEQUENTIAL -> Icons.Filled.Replay
                        PlayMode.SHUFFLE -> Icons.Filled.Shuffle
                        PlayMode.REPEAT_ONE -> Icons.Filled.RepeatOneOn
                    },
                    contentDescription = "播放模式"
                )
            }
            
            // 上一曲
            IconButton(onClick = { viewModel.previous() }) {
                Icon(
                    Icons.Filled.SkipPrevious,
                    contentDescription = "上一曲",
                    modifier = Modifier.size(40.dp)
                )
            }
            
            // 播放/暂停
            FilledIconButton(
                onClick = { viewModel.togglePlayPause() },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    if (playbackState is PlaybackState.Playing) 
                        Icons.Filled.Pause
                    else 
                        Icons.Filled.PlayArrow,
                    contentDescription = "播放/暂停",
                    modifier = Modifier.size(32.dp)
                )
            }
            
            // 下一曲
            IconButton(onClick = { viewModel.next() }) {
                Icon(
                    Icons.Filled.SkipNext,
                    contentDescription = "下一曲",
                    modifier = Modifier.size(40.dp)
                )
            }
            
            // 喜欢
            IconButton(onClick = { viewModel.toggleFavorite(currentSong) }) {
                Icon(
                    Icons.Default.FavoriteBorder,
                    contentDescription = "喜欢"
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

/**
 * 格式化时间（毫秒转分:秒）
 */
private fun formatTime(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / 1000) / 60
    return String.format("%02d:%02d", minutes, seconds)
}
