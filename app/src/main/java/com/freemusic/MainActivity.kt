package com.freemusic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.freemusic.ui.screen.*
import com.freemusic.ui.theme.FreeMusicTheme
import com.freemusic.viewmodel.MainViewModel

/**
 * 主Activity
 * 作为应用的入口点，管理导航和UI
 */
class MainActivity : ComponentActivity() {
    
    // ViewModel
    private val viewModel: MainViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            FreeMusicTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FreeMusicApp(viewModel)
                }
            }
        }
    }
}

/**
 * 应用主界面组合函数
 */
@Composable
fun FreeMusicApp(viewModel: MainViewModel) {
    val navController = rememberNavController()
    var selectedIndex by remember { mutableStateOf(0) }
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Text("🏠") },
                    label = { Text("首页") },
                    selected = selectedIndex == 0,
                    onClick = {
                        selectedIndex = 0
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
                
                NavigationBarItem(
                    icon = { Text("🔍") },
                    label = { Text("搜索") },
                    selected = selectedIndex == 1,
                    onClick = {
                        selectedIndex = 1
                        navController.navigate("search") {
                            popUpTo("home")
                        }
                    }
                )
                
                NavigationBarItem(
                    icon = { Text("📚") },
                    label = { Text("音乐库") },
                    selected = selectedIndex == 2,
                    onClick = {
                        selectedIndex = 2
                        navController.navigate("library") {
                            popUpTo("home")
                        }
                    }
                )
                
                NavigationBarItem(
                    icon = { Text("⚙️") },
                    label = { Text("设置") },
                    selected = selectedIndex == 3,
                    onClick = {
                        selectedIndex = 3
                        navController.navigate("settings") {
                            popUpTo("home")
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") { HomeScreen(viewModel, navController) }
            composable("search") { SearchScreen(viewModel) }
            composable("library") { LibraryScreen(viewModel) }
            composable("settings") { SettingsScreen() }
            composable("player") { PlayerScreen(viewModel) }
        }
    }
}
