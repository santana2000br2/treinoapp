package com.example.treinoapp

import android.content.Context

enum class ThemeMode {
    /** Segue a definição claro/escuro do telemóvel */
    SYSTEM,
    /** Sempre claro */
    LIGHT,
    /** Sempre noturno (escuro) */
    DARK,
}

/** Aparência do menu inicial: ícones simples ou cartões com ilustrações. */
enum class HomeVisualStyle {
    /** Claro/escuro simples, sem PNG nos cartões (como antes). */
    SIMPLE,
    /** Cartões com ilustrações; no escuro usa PNG claras até existirem as versões escuras. */
    ILLUSTRATED,
}

object ThemePreferences {
    private const val PREFS_NAME = "treinoapp_settings"
    private const val KEY_THEME = "theme_mode"
    private const val KEY_HOME_VISUAL = "home_visual_style"

    fun getMode(context: Context): ThemeMode {
        val name = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_THEME, ThemeMode.SYSTEM.name)
        return ThemeMode.entries.find { it.name == name } ?: ThemeMode.SYSTEM
    }

    fun setMode(context: Context, mode: ThemeMode) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_THEME, mode.name)
            .apply()
    }

    fun getHomeVisualStyle(context: Context): HomeVisualStyle {
        val name = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_HOME_VISUAL, HomeVisualStyle.SIMPLE.name)
        return HomeVisualStyle.entries.find { it.name == name } ?: HomeVisualStyle.SIMPLE
    }

    fun setHomeVisualStyle(context: Context, style: HomeVisualStyle) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_HOME_VISUAL, style.name)
            .apply()
    }
}
