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

object ThemePreferences {
    private const val PREFS_NAME = "treinoapp_settings"
    private const val KEY_THEME = "theme_mode"

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
}
