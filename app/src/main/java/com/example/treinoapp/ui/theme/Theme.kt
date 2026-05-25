package com.example.treinoapp.ui.theme

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val SlateDarkColorScheme = darkColorScheme(
    primary = Color(0xFF38BDF8),
    onPrimary = SlateInk,
    primaryContainer = Color(0xFF0369A1),
    onPrimaryContainer = Color(0xFFE0F2FE),
    secondary = Color(0xFF94A3B8),
    onSecondary = SlateInk,
    secondaryContainer = Color(0xFF334155),
    onSecondaryContainer = Color(0xFFE2E8F0),
    tertiary = Color(0xFF7DD3FC),
    onTertiary = SlateInk,
    tertiaryContainer = Color(0xFF0C4A6E),
    onTertiaryContainer = Color(0xFFE0F2FE),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color.Transparent,
    onBackground = Color(0xFFF1F5F9),
    surface = Color(0xFF1E293B).copy(alpha = 0.65f),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF64748B),
    outlineVariant = Color(0xFF475569),
    scrim = SlateInk,
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color.Transparent,
    onBackground = Color(0xFF0F172A),
    surface = Color.White.copy(alpha = 0.78f),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF334155),
)

@Composable
fun TreinoAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    /** Se true (e API 31+), usa cores dinâmicas do sistema em vez do fundo slate. */
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> SlateDarkColorScheme
        else -> LightColorScheme
    }

    val gradientBrush = gradientBrushForTheme(darkTheme, dynamicColor)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
    ) {
        if (gradientBrush != null) {
            Box(Modifier.fillMaxSize()) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(gradientBrush),
                )
                Box(Modifier.fillMaxSize()) { content() }
            }
        } else {
            content()
        }
    }
}

private fun gradientBrushForTheme(darkTheme: Boolean, dynamicColor: Boolean): Brush? {
    if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) return null
    return if (darkTheme) {
        Brush.verticalGradient(colors = listOf(SlateBgTop, SlateBgBottom))
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFF8FAFC),
                Color(0xFFE2E8F0),
            ),
        )
    }
}
