package com.freemusic.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * 深色配色方案
 */
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF8B5CF6),      // 紫色主色
    secondary = Color(0xFFEC4899),    // 粉色辅助色
    tertiary = Color(0xFF06B6D4),     // 青色
    background = Color(0xFF0F172A),   // 深色背景
    surface = Color(0xFF1E293B),      // 卡片背景
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFE2E8F0),
    onSurface = Color(0xFFE2E8F0)
)

/**
 * 浅色配色方案
 */
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF8B5CF6),      // 紫色主色
    secondary = Color(0xFFEC4899),    // 粉色辅助色
    tertiary = Color(0xFF06B6D4),     // 青色
    background = Color(0xFFF8FAFC),   // 浅色背景
    surface = Color.White,            // 卡片背景
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1E293B),
    onSurface = Color(0xFF1E293B)
)

/**
 * 自由音主题
 * 
 * @param darkTheme 是否使用深色主题
 * @param dynamicColor 是否启用动态颜色（Android 12+）
 * @param content 内容组合函数
 */
@Composable
fun FreeMusicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,  // 启用动态颜色
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Android 12+ 支持动态颜色
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        // 使用预定义配色
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
