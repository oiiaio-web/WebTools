package com.oiiaio.web.tools.tools.network

import android.app.ActivityManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.widget.LinearLayout
import com.oiiaio.web.tools.ui.BaseToolActivity
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.URL
import kotlin.math.sqrt

class DeviceInfoActivity : BaseToolActivity() {
    override fun toolTitle() = "设备 / 网络信息"

    private fun localIp(): String {
        try {
            NetworkInterface.getNetworkInterfaces().toList().forEach { ni ->
                ni.inetAddresses.toList().forEach { addr ->
                    if (!addr.isLoopbackAddress && addr is Inet4Address) return addr.hostAddress ?: ""
                }
            }
        } catch (_: Exception) {
        }
        return "未获取"
    }

    private fun publicIp(): String {
        val endpoints = listOf("https://api.ipify.org", "https://ifconfig.me/ip")
        for (ep in endpoints) {
            try {
                val conn = URL(ep).openConnection()
                conn.connectTimeout = 8000
                conn.readTimeout = 8000
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android)")
                return conn.getInputStream().bufferedReader().readText().trim()
            } catch (_: Exception) {
            }
        }
        return "获取失败（无网络？）"
    }

    override fun onBuild() {
        // device
        label("设备信息", 4)
        card {
            addView(infoRow("品牌", Build.BRAND))
            addView(infoRow("制造商", Build.MANUFACTURER))
            addView(infoRow("型号", Build.MODEL))
            addView(infoRow("设备名", Build.DEVICE))
            addView(infoRow("Android 版本", Build.VERSION.RELEASE))
            addView(infoRow("SDK 版本", Build.VERSION.SDK_INT.toString()))
            addView(infoRow("安全补丁", Build.VERSION.SECURITY_PATCH ?: "-"))
            addView(infoRow("CPU 架构", Build.SUPPORTED_ABIS.joinToString(", ")))
        }

        // screen
        val dm = resources.displayMetrics
        val inches = sqrt((dm.widthPixels * dm.widthPixels + dm.heightPixels * dm.heightPixels).toDouble()) / dm.xdpi
        label("屏幕")
        card {
            addView(infoRow("分辨率", "${dm.widthPixels} × ${dm.heightPixels} px"))
            addView(infoRow("逻辑密度", "${dm.density} (${dm.densityDpi} dpi)"))
            addView(infoRow("字体缩放", dm.scaledDensity.toString()))
            addView(infoRow("屏幕尺寸（约）", "%.2f 英寸".format(inches)))
        }

        // memory / storage
        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val mi = ActivityManager.MemoryInfo()
        am.getMemoryInfo(mi)
        val stat = StatFs(Environment.getDataDirectory().path)
        label("存储")
        card {
            addView(infoRow(
                "运行内存",
                "%.1f GB（可用 %.1f GB）".format(
                    mi.totalMem / 1073741824.0, mi.availMem / 1073741824.0
                )
            ))
            addView(infoRow(
                "内部存储",
                "%.1f GB（可用 %.1f GB）".format(
                    stat.totalBytes / 1e9, stat.availableBytes / 1e9
                )
            ))
        }

        // network
        label("网络")
        val host = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        card {
            addView(host)
            addView(row(primaryBtn("🔄  刷新本机 IP") { fillNetwork(host) }))
        }
        fillNetwork(host)

        // app
        val pi = packageManager.getPackageInfo(packageName, 0)
        label("应用")
        card {
            addView(infoRow("包名", packageName))
            addView(infoRow("版本", pi.versionName))
            addView(infoRow("目标 SDK", applicationInfo.targetSdkVersion.toString()))
        }
    }

    private fun fillNetwork(host: LinearLayout) {
        host.removeAllViews()
        host.addView(infoRow("本机 IP（内网）", "获取中…"))
        host.addView(infoRow("公网 IP", "获取中…"))
        Thread {
            val local = localIp()
            val pub = publicIp()
            runOnUiThread {
                host.removeAllViews()
                host.addView(infoRow("本机 IP（内网）", local))
                host.addView(infoRow("公网 IP", pub))
            }
        }.start()
    }
}
