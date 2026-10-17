package com.oiiaio.web.tools

import android.app.Activity
import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes

object ThemeEngine {
    const val DARK = 0
    const val LIGHT = 1
    const val AMOLED = 2

    const val PREFS = "webtools_prefs"
    const val KEY_THEME = "theme"

    fun current(ctx: Context): Int =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(KEY_THEME, DARK)

    fun setTheme(ctx: Context, value: Int) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putInt(KEY_THEME, value).apply()
    }

    @StyleRes
    fun styleFor(theme: Int): Int = when (theme) {
        LIGHT -> R.style.Theme_WebTools_Light
        AMOLED -> R.style.Theme_WebTools_Amoled
        else -> R.style.Theme_WebTools
    }

    fun apply(activity: Activity) {
        activity.setTheme(styleFor(current(activity)))
    }

    fun attrColor(ctx: Context, @AttrRes attr: Int): Int {
        val tv = TypedValue()
        ctx.theme.resolveAttribute(attr, tv, true)
        return tv.data
    }

    fun isDark(ctx: Context): Boolean = current(ctx) != LIGHT
}
