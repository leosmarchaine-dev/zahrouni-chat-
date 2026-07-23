package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

enum class ThemeMode {
  LIGHT,
  DARK,
  AMOLED,
  SYSTEM
}

private val LightColorScheme = lightColorScheme(
  primary = TunisiaRedPrimary,
  onPrimary = Color.White,
  primaryContainer = TunisiaRedContainerLight,
  onPrimaryContainer = TunisiaRedDark,
  secondary = SidiBouSaidBlue,
  onSecondary = Color.White,
  tertiary = GoldenCrescent,
  background = LightBackground,
  onBackground = LightOnSurface,
  surface = LightSurface,
  onSurface = LightOnSurface,
  surfaceVariant = LightSurfaceVariant,
  onSurfaceVariant = Color(0xFF44474E)
)

private val DarkColorScheme = darkColorScheme(
  primary = TunisiaRedLight,
  onPrimary = Color.White,
  primaryContainer = TunisiaRedContainerDark,
  onPrimaryContainer = Color(0xFFFFDADA),
  secondary = SidiBouSaidBlue,
  onSecondary = Color.White,
  tertiary = GoldenCrescent,
  background = DarkBackground,
  onBackground = DarkOnSurface,
  surface = DarkSurface,
  onSurface = DarkOnSurface,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = Color(0xFFC4C6C9)
)

private val AmoledColorScheme = darkColorScheme(
  primary = TunisiaRedLight,
  onPrimary = Color.White,
  primaryContainer = TunisiaRedContainerDark,
  onPrimaryContainer = Color(0xFFFFDADA),
  secondary = SidiBouSaidBlue,
  onSecondary = Color.White,
  tertiary = GoldenCrescent,
  background = AmoledBackground,
  onBackground = Color.White,
  surface = AmoledSurface,
  onSurface = Color.White,
  surfaceVariant = AmoledSurfaceVariant,
  onSurfaceVariant = Color(0xFFC4C6C9)
)

@Composable
fun TunisiaChatTheme(
  themeMode: ThemeMode = ThemeMode.SYSTEM,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit
) {
  val isDark = when (themeMode) {
    ThemeMode.LIGHT -> false
    ThemeMode.DARK, ThemeMode.AMOLED -> true
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
  }

  val context = LocalContext.current
  val colorScheme = when {
    themeMode == ThemeMode.AMOLED -> AmoledColorScheme
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    isDark -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

