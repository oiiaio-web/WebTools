package com.oiiaio.web.tools.model

import androidx.annotation.ColorRes
import com.oiiaio.web.tools.R
import com.oiiaio.web.tools.tools.crypto.AesActivity
import com.oiiaio.web.tools.tools.crypto.DesActivity
import com.oiiaio.web.tools.tools.crypto.HashActivity
import com.oiiaio.web.tools.tools.crypto.HmacActivity
import com.oiiaio.web.tools.tools.crypto.PasswordGeneratorActivity
import com.oiiaio.web.tools.tools.crypto.RsaActivity
import com.oiiaio.web.tools.tools.crypto.UuidActivity
import com.oiiaio.web.tools.tools.design.ColorActivity
import com.oiiaio.web.tools.tools.design.CssGradientActivity
import com.oiiaio.web.tools.tools.design.CssShadowActivity
import com.oiiaio.web.tools.tools.encode.Base32Activity
import com.oiiaio.web.tools.tools.encode.Base64Activity
import com.oiiaio.web.tools.tools.encode.HexActivity
import com.oiiaio.web.tools.tools.encode.HtmlEntityActivity
import com.oiiaio.web.tools.tools.encode.MorseActivity
import com.oiiaio.web.tools.tools.encode.RadixActivity
import com.oiiaio.web.tools.tools.encode.UnicodeActivity
import com.oiiaio.web.tools.tools.encode.UrlEncodeActivity
import com.oiiaio.web.tools.tools.network.BrowserActivity
import com.oiiaio.web.tools.tools.network.DeviceInfoActivity
import com.oiiaio.web.tools.tools.network.HttpClientActivity
import com.oiiaio.web.tools.tools.network.IpSubnetActivity
import com.oiiaio.web.tools.tools.network.PageSourceActivity
import com.oiiaio.web.tools.tools.network.StatusCodesActivity
import com.oiiaio.web.tools.tools.network.UrlParseActivity
import com.oiiaio.web.tools.tools.network.UserAgentActivity
import com.oiiaio.web.tools.tools.system.AboutActivity
import com.oiiaio.web.tools.tools.system.SettingsActivity
import com.oiiaio.web.tools.tools.text.DiffActivity
import com.oiiaio.web.tools.tools.text.JsonActivity
import com.oiiaio.web.tools.tools.text.RegexActivity
import com.oiiaio.web.tools.tools.text.TextStatsActivity
import com.oiiaio.web.tools.tools.text.TextTransformActivity
import com.oiiaio.web.tools.tools.text.TimestampActivity
import com.oiiaio.web.tools.tools.text.XmlActivity
import com.oiiaio.web.tools.tools.web.HtmlRunnerActivity
import com.oiiaio.web.tools.tools.web.ImageBase64Activity
import com.oiiaio.web.tools.tools.web.JsonToKotlinActivity
import com.oiiaio.web.tools.tools.web.QrGeneratorActivity

data class ToolCategory(val id: String, val name: String, val emoji: String, @ColorRes val color: Int)

data class Tool(val name: String, val desc: String, val emoji: String, val target: Class<*>)

data class Section(val category: ToolCategory, val tools: List<Tool>)

object Categories {
    val ENCODE = ToolCategory("encode", "编码 / 解码", "🔐", R.color.cat_encode)
    val CRYPTO = ToolCategory("crypto", "加密 / 安全", "🛡️", R.color.cat_crypto)
    val TEXT = ToolCategory("text", "文本 / 格式化", "📝", R.color.cat_text)
    val NETWORK = ToolCategory("network", "网络 / 请求", "🌐", R.color.cat_network)
    val WEB = ToolCategory("web", "前端 / 网页", "🧩", R.color.cat_web)
    val DESIGN = ToolCategory("design", "设计 / 颜色", "🎨", R.color.cat_design)
    val SYSTEM = ToolCategory("system", "系统 / 其他", "⚙️", R.color.cat_system)
}

object ToolRegistry {

    val sections: List<Section> = listOf(
        Section(Categories.ENCODE, listOf(
            Tool("Base64 编解码", "文本与 Base64 互转，支持 URL Safe", "🔡", Base64Activity::class.java),
            Tool("URL 编解码", "网址与查询参数编码解码", "🔗", UrlEncodeActivity::class.java),
            Tool("Unicode 转义", "中文与 \\uXXXX 转义互转", "🔤", UnicodeActivity::class.java),
            Tool("HEX 十六进制", "字节与十六进制互转", "🔢", HexActivity::class.java),
            Tool("Base32", "RFC 4648 Base32 编解码", "🔠", Base32Activity::class.java),
            Tool("HTML 实体", "特殊字符与实体互转", "🏷️", HtmlEntityActivity::class.java),
            Tool("摩斯电码", "文本与摩斯电码互转", "📡", MorseActivity::class.java),
            Tool("进制转换", "二 / 八 / 十 / 十六进制互转", "🔟", RadixActivity::class.java)
        )),
        Section(Categories.CRYPTO, listOf(
            Tool("哈希计算", "MD5 / SHA 系列 / CRC32 散列", "#️⃣", HashActivity::class.java),
            Tool("HMAC 认证", "HmacMD5 / HmacSHA 系列", "🔏", HmacActivity::class.java),
            Tool("随机密码", "自定义字符集强密码生成", "🔑", PasswordGeneratorActivity::class.java),
            Tool("UUID 生成", "批量生成 v4 UUID", "🪪", UuidActivity::class.java),
            Tool("AES 加解密", "CBC / ECB，128/192/256 位", "🔐", AesActivity::class.java),
            Tool("DES 加解密", "经典对称加密算法", "🔒", DesActivity::class.java),
            Tool("RSA 加解密", "非对称加密与数字签名", "🧷", RsaActivity::class.java)
        )),
        Section(Categories.TEXT, listOf(
            Tool("JSON 工具", "格式化、压缩、合法性校验", "🧾", JsonActivity::class.java),
            Tool("XML 格式化", "缩进美化与合法性校验", "📄", XmlActivity::class.java),
            Tool("正则测试", "正则匹配与分组捕获", "🔍", RegexActivity::class.java),
            Tool("文本统计", "字数 / 行数 / 字节 / 中英", "📊", TextStatsActivity::class.java),
            Tool("文本处理", "大小写 / 去重 / 排序 / 去空", "🔁", TextTransformActivity::class.java),
            Tool("时间戳转换", "Unix 时间戳与日期互转", "🕐", TimestampActivity::class.java),
            Tool("文本对比", "逐行 Diff 高亮差异", "🌓", DiffActivity::class.java)
        )),
        Section(Categories.NETWORK, listOf(
            Tool("HTTP 请求", "REST 接口调试（类 Postman）", "📮", HttpClientActivity::class.java),
            Tool("网页源码", "抓取 URL 的 HTML 源码", "📃", PageSourceActivity::class.java),
            Tool("内置浏览器", "地址栏浏览任意网页", "🌐", BrowserActivity::class.java),
            Tool("设备 / 网络信息", "硬件 / 屏幕 / 内存 / IP", "📱", DeviceInfoActivity::class.java),
            Tool("IP 子网计算", "CIDR / 掩码 / 广播地址", "🧮", IpSubnetActivity::class.java),
            Tool("URL 解析", "拆分协议 / 主机 / 参数", "🧩", UrlParseActivity::class.java),
            Tool("User-Agent", "常见浏览器 UA 一键复制", "🦊", UserAgentActivity::class.java),
            Tool("HTTP 状态码", "1xx-5xx 状态码含义大全", "🚦", StatusCodesActivity::class.java)
        )),
        Section(Categories.WEB, listOf(
            Tool("HTML 运行器", "离线运行 HTML / CSS / JS", "▶️", HtmlRunnerActivity::class.java),
            Tool("二维码生成", "文本 / 网址生成二维码", "🔳", QrGeneratorActivity::class.java),
            Tool("图片转 Base64", "图片与 data URI 互转", "🖼️", ImageBase64Activity::class.java),
            Tool("JSON 转 Kotlin", "自动生成 data class", "🧱", JsonToKotlinActivity::class.java)
        )),
        Section(Categories.DESIGN, listOf(
            Tool("颜色转换", "HEX / RGB / HSL 取色", "🎨", ColorActivity::class.java),
            Tool("CSS 渐变", "线性渐变代码生成", "🌈", CssGradientActivity::class.java),
            Tool("CSS 阴影", "box-shadow 实时预览", "💧", CssShadowActivity::class.java)
        )),
        Section(Categories.SYSTEM, listOf(
            Tool("设置", "主题切换、外观与偏好", "🎛️", SettingsActivity::class.java),
            Tool("关于", "应用信息、版本与许可", "ℹ️", AboutActivity::class.java)
        ))
    )

    /** All tools flattened for search. */
    val all: List<Tool> get() = sections.flatMap { it.tools }
}
