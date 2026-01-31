package com.freemusic.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.freemusic.viewmodel.MainViewModel

/**
 * 音乐库界面
 * 显示收藏的歌曲、播放历史等
 */
@Composable
fun LibraryScreen(viewModel: MainViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    val favoriteSongs by viewModel.favoriteSongs.collectAsState()
    val playHistory by viewModel.playHistory.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize()) {
        // 标签页
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("❤️ 我喜欢") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("⏱️ 历史") }
            )
        }
        
        // 内容区域
        when (selectedTab) {
            0 -> FavoriteSongsTab(favoriteSongs, viewModel)
            1 -> PlayHistoryTab(playHistory, viewModel)
        }
    }
}

/**
 * 我喜欢的音乐标签页
 */
@Composable
fun FavoriteSongsTab(
    songs: List<com.freemusic.model.Song>,
    viewModel: MainViewModel
) {
    if (songs.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "💔",
                    style = MaterialTheme.typography.displayLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "还没有喜欢的音乐",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "点击歌曲的爱心按钮添加收藏",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "共 ${songs.size} 首歌曲",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            items(songs) { song ->
                SongListItem(
                    song = song,
                    onClick = { viewModel.playSong(song) }
                )
            }
        }
    }
}

/**
 * 播放历史标签页
 */
@Composable
fun PlayHistoryTab(
    songs: List<com.freemusic.model.Song>,
    viewModel: MainViewModel
) {
    if (songs.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📝",
                    style = MaterialTheme.typography.displayLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "暂无播放历史",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(songs) { song ->
                SongListItem(
                    song = song,
                    onClick = { viewModel.playSong(song) }
                )
            }
        }
    }
}
