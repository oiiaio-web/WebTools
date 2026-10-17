package com.oiiaio.web.tools.tools.network

import android.widget.LinearLayout
import com.oiiaio.web.tools.ui.BaseToolActivity

class IpSubnetActivity : BaseToolActivity() {
    override fun toolTitle() = "IP 子网 / CIDR 计算"

    private lateinit var host: LinearLayout

    private fun strToLong(ip: String): Long {
        val a = ip.trim().split(".")
        require(a.size == 4) { "IP 必须是 4 段" }
        var result = 0L
        for (seg in a) {
            val n = seg.toIntOrNull()
            require(n != null && n in 0..255) { "每段需在 0-255" }
            result = (result shl 8) or n.toLong()
        }
        return result
    }

    private fun longToStr(v: Long): String =
        "${(v shr 24) and 255}.${(v shr 16) and 255}.${(v shr 8) and 255}.${v and 255}"

    override fun onBuild() {
        label("输入 CIDR（IPv4）")
        val input = input("例如 192.168.1.10/24", mono = true)
        addView(input)
        addView(row(primaryBtn("计算") {
            calc(input.text.toString().trim())
        }))
        label("计算结果")
        host = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        card { addView(host) }
    }

    private fun calc(cidr: String) {
        host.removeAllViews()
        try {
            val parts = cidr.split("/")
            require(parts.size == 2) { "格式应为 IP/前缀，如 192.168.1.0/24" }
            val ip = strToLong(parts[0])
            val prefix = parts[1].toIntOrNull()
            require(prefix != null && prefix in 0..32) { "前缀需在 0-32" }
            val mask = if (prefix == 0) 0L else (0xFFFFFFFFL shl (32 - prefix)) and 0xFFFFFFFFL
            val network = ip and mask
            val broadcast = network or (mask.inv() and 0xFFFFFFFFL)
            val total = broadcast - network + 1
            val (firstHost, lastHost, usable) = when {
                prefix == 32 -> Triple(network, network, 1L)
                prefix == 31 -> Triple(network, broadcast, 2L)
                else -> Triple(network + 1, broadcast - 1, (total - 2).coerceAtLeast(0))
            }
            host.addView(infoRow("IP 地址", longToStr(ip)))
            host.addView(infoRow("前缀长度", "/$prefix"))
            host.addView(infoRow("子网掩码", longToStr(mask)))
            host.addView(infoRow("通配符掩码", longToStr(mask.inv() and 0xFFFFFFFFL)))
            host.addView(infoRow("网络地址", longToStr(network)))
            host.addView(infoRow("广播地址", longToStr(broadcast)))
            host.addView(infoRow("首个可用地址", longToStr(firstHost)))
            host.addView(infoRow("末个可用地址", longToStr(lastHost)))
            host.addView(infoRow("可用主机数", usable.toString()))
            host.addView(infoRow("地址总数", total.toString()))
        } catch (e: Exception) {
            host.addView(infoRow("错误", e.message ?: "输入不正确", false))
            toast("计算失败")
        }
    }
}
