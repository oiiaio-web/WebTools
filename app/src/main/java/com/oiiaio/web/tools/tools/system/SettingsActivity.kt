package com.oiiaio.web.tools.tools.system

import android.content.Intent
import com.oiiaio.web.tools.ThemeEngine
import com.oiiaio.web.tools.ui.BaseToolActivity

class SettingsActivity : BaseToolActivity() {

    override fun toolTitle() = "设置"

    override fun onBuild() {
        label("外观主题")
        card {
            val current = ThemeEngine.current(this@SettingsActivity)
            data class T(val title: String, val id: Int)
            val themes = listOf(
                T("🌙  深色（默认）", ThemeEngine.DARK),
                T("☀️  浅色", ThemeEngine.LIGHT),
                T("⚫  纯黑 AMOLED", ThemeEngine.AMOLED)
            )
            themes.forEach { t ->
                val btn = if (t.id == current) primaryBtn("${t.title}    ✓") {}
                else ghostBtn(t.title) {}
                btn.setOnClickListener {
                    ThemeEngine.setTheme(this@SettingsActivity, t.id)
                    recreate()
                }
                addView(row(btn))
            }
        }
        note("切换主题后立即生效，无需重启应用。")

        label("数据与存储")
        card {
            val clear = primaryBtn("🧹  清除缓存") {
                var size = 0L
                cacheDir.walkTopDown().forEach { f ->
                    if (f.isFile) size += f.length()
                    if (f != cacheDir) f.delete()
                }
                toast("已清理缓存 %.1f KB".format(size / 1024.0))
            }
            addView(row(clear))
        }

        label("关于")
        card {
            val version = packageManager.getPackageInfo(packageName, 0).versionName
            val about = ghostBtn("ℹ️  关于 Web 工具箱（$version）") {
                startActivity(Intent(this@SettingsActivity, AboutActivity::class.java))
            }
            addView(row(about))
        }
    }
}
