package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val OpenKitDarkColorScheme = darkColorScheme(
  primary = Primary,
  onPrimary = OnPrimary,
  primaryContainer = PrimaryContainer,
  onPrimaryContainer = OnPrimaryContainer,
  secondary = AccentGold,
  onSecondary = Color(0xFF422006),
  tertiary = AccentCyan,
  background = StudioBg,
  onBackground = TextPrimary,
  surface = StudioPanelBg,
  onSurface = TextPrimary,
  surfaceVariant = StudioPanelElevated,
  onSurfaceVariant = TextSecondary,
  outline = StudioBorder,
  outlineVariant = StudioBorderLight,
  error = AccentRed,
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to true for Studio dark mode
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = OpenKitDarkColorScheme,
    typography = Typography,
    content = content
  )
}
