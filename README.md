# WebTools · Web 工具箱

一款用 **Kotlin** 编写的 Android 开发工具合集，内置 **39 个工具**，深色系界面，支持三套主题（深色 / 浅色 / AMOLED）。

- 包名：`com.oiiaio.web.tools`
- 最低版本：minSdk 26（Android 8.0），targetSdk / compileSdk 34
- 权限：仅 `INTERNET`、`ACCESS_NETWORK_STATE`
- 体积：约 5.2 MB（未混淆）

## 功能清单（39 个）

### 🔐 编码 / 解码（8）
Base64（含 URL Safe）、URL 编解码、Unicode 转义、HEX 十六进制、Base32、HTML 实体、摩斯电码、进制转换

### 🛡️ 加密 / 安全（7）
哈希计算（MD5 / SHA 系列 / CRC32）、HMAC、随机密码生成器、UUID 批量生成、AES（CBC/ECB）、DES、RSA（密钥对 / 加解密 / 签名验签）

### 📝 文本 / 格式化（7）
JSON、XML、正则测试、文本统计、文本处理（大小写/去重/排序/反转）、时间戳转换、逐行 Diff

### 🌐 网络 / 请求（8）
HTTP 请求调试（类 Postman）、网页源码抓取、内置浏览器、设备与网络信息、IP 子网 CIDR 计算、URL 解析、User-Agent、HTTP 状态码大全

### 🧩 前端 / 网页（4）
HTML 运行器（离线运行 HTML/CSS/JS）、二维码生成、图片转 Base64、JSON 转 Kotlin data class

### 🎨 设计 / 颜色（3）
颜色转换（HEX / RGB / HSL）、CSS 渐变生成器、CSS 阴影生成器（WebView 实时渲染）

### ⚙️ 系统 / 其他（2）
设置（三主题切换）、关于

## 技术栈

| 组件 | 选型 |
|---|---|
| 语言 | Kotlin 1.9.24 |
| 构建 | Gradle 8.9 + AGP 8.3.2 + JDK 17 |
| UI | AndroidX（appcompat、Material、RecyclerView、ConstraintLayout） |
| 加密 | JDK 内置 `javax.crypto` / `java.security` |
| 二维码 | ZXing core 3.5.3 |
| HTTP | 系统 `HttpURLConnection`（无第三方网络库） |
| JSON | 系统 `org.json` |
| 网页类 | WebView |

## 自行构建

```bash
# 需要 JDK 17 与 Android SDK（compileSdk 34）
gradle :app:assembleRelease
# 产物：app/build/outputs/apk/release/app-release-unsigned.apk
# 用 build-tools 的 zipalign 对齐 + apksigner 签名即可安装
```

> 说明：签名 keystore 不纳入版本库（见 .gitignore），请自行生成并保管。
