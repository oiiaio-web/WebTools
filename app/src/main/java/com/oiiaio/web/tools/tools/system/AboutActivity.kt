package com.oiiaio.web.tools.tools.system

import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.pm.PackageInfoCompat
import com.oiiaio.web.tools.model.ToolRegistry
import com.oiiaio.web.tools.ui.BaseToolActivity

class AboutActivity : BaseToolActivity() {

    override fun toolTitle() = "关于"

    override fun onBuild() {
        val pi = packageManager.getPackageInfo(packageName, 0)
        val version = pi.versionName
        val code = PackageInfoCompat.getLongVersionCode(pi)

        // Hero
        val hero = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(0, dp(20f), 0, dp(6f))
        }
        val icon = TextView(this).apply {
            text = "🧰"
            textSize = 42f
            gravity = Gravity.CENTER
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor((0x22 shl 24) or (cPrimary and 0x00FFFFFF))
            }
            layoutParams = LinearLayout.LayoutParams(dp(88f), dp(88f))
        }
        hero.addView(icon)
        hero.addView(TextView(this).apply {
            text = "Web 工具箱"
            setTextColor(cText)
            textSize = 22f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER
            setPadding(0, dp(14f), 0, 0)
        })
        hero.addView(TextView(this).apply {
            text = "WebTools · 开发者随身工具集"
            setTextColor(cSub)
            textSize = 12.5f
            gravity = Gravity.CENTER
            setPadding(0, dp(3f), 0, 0)
        })
        addView(hero)

        label("应用信息")
        card {
            addView(infoRow("版本", "v$version ($code)"))
            addView(infoRow("包名", packageName))
            addView(infoRow("工具数量", "${ToolRegistry.all.size} 个"))
            addView(infoRow("最低系统", "Android 8.0 (API 26)"))
            addView(infoRow("目标系统", "Android 14 (API 34)"))
        }

        label("应用简介")
        card {
            addView(TextView(this@AboutActivity).apply {
                text = "一款面向开发者与极客的离线工具箱，集成编解码、加密哈希、文本格式化、网络请求调试、前端运行、二维码、颜色设计等 30+ 常用功能，界面简洁、无需联网即可运行大部分工具。"
                setTextColor(cText)
                textSize = 13.5f
                setLineSpacing(dp(3f).toFloat(), 1f)
            })
        }

        label("功能分类")
        card {
            ToolRegistry.sections.forEach { s ->
                addView(infoRow("${s.category.emoji}  ${s.category.name}", "${s.tools.size} 个", false))
            }
        }

        label("技术栈")
        card {
            addView(infoRow("语言", "Kotlin", false))
            addView(infoRow("界面", "Material Design 3", false))
            addView(infoRow("基础", "AndroidX / AppCompat", false))
            addView(infoRow("网页", "WebView / HttpURLConnection", false))
            addView(infoRow("二维码", "ZXing Core", false))
        }

        label("开源许可")
        card {
            addView(TextView(this@AboutActivity).apply {
                text = "• ZXing core —— Apache License 2.0\n• AndroidX / Material Components —— Apache License 2.0"
                setTextColor(cSub)
                textSize = 12.5f
                setLineSpacing(dp(3f).toFloat(), 1f)
            })
        }

        note("免责声明：本应用仅用于学习与开发调试，网络请求与网页相关功能请在遵守当地法律法规及目标网站协议的前提下使用。")
        addView(TextView(this@AboutActivity).apply {
            text = "Made with Kotlin"
            setTextColor(cSub)
            textSize = 11.5f
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(18f) }
        })
    }
}
