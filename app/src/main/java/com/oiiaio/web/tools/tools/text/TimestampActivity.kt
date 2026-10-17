package com.oiiaio.web.tools.tools.text

import android.widget.LinearLayout
import com.oiiaio.web.tools.ui.BaseToolActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class TimestampActivity : BaseToolActivity() {
    override fun toolTitle() = "时间戳转换"

    private val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val utcFmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private var isSeconds = true

    override fun onBuild() {
        // current time
        label("当前时间", 4)
        val nowHost = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        card {
            addView(nowHost)
            addView(row(primaryBtn("🔄  刷新当前时间") { renderNow(nowHost) }))
        }
        renderNow(nowHost)

        // ts -> date
        label("时间戳 → 日期")
        card {
            val tsInput = input("输入时间戳，如 1700000000", mono = true)
            addView(tsInput)
            addView(segmented(listOf("秒（10 位）", "毫秒（13 位）")) { idx, _ -> isSeconds = idx == 0 })
            addView(row(primaryBtn("转换为日期") {
                val raw = tsInput.text.toString().trim()
                val num = raw.toLongOrNull()
                if (num == null) {
                    toast("请输入数字时间戳")
                    return@primaryBtn
                }
                val ms = if (isSeconds) num * 1000 else num
                toast("已转换")
                tsInput.setText("")
                addResultToDate(ms)
            }))
        }

        // date -> ts
        label("日期 → 时间戳")
        card {
            val dateInput = input("yyyy-MM-dd HH:mm:ss（本地时区）", mono = true)
            addView(dateInput)
            addView(row(primaryBtn("转换为时间戳") {
                val raw = dateInput.text.toString().trim()
                try {
                    val ms = fmt.parse(raw)!!.time
                    dateInput.setText("")
                    addResultFromDate(ms)
                } catch (e: Exception) {
                    toast("日期格式应为 yyyy-MM-dd HH:mm:ss")
                }
            }))
        }
    }

    private fun renderNow(host: LinearLayout) {
        host.removeAllViews()
        val ms = System.currentTimeMillis()
        host.addView(infoRow("时间戳（秒）", (ms / 1000).toString()))
        host.addView(infoRow("时间戳（毫秒）", ms.toString()))
        host.addView(infoRow("本地时间", fmt.format(Date(ms))))
        host.addView(infoRow("UTC 时间", utcFmt.format(Date(ms))))
        host.addView(infoRow("时区", TimeZone.getDefault().displayName))
    }

    private fun weekday(ms: Long): String {
        val c = Calendar.getInstance()
        c.timeInMillis = ms
        return "星期" + "日一二三四五六"[c.get(Calendar.DAY_OF_WEEK) - 1]
    }

    private fun addResultToDate(ms: Long) {
        label("转换结果（时间戳 → 日期）")
        card {
            addView(infoRow("本地时间", fmt.format(Date(ms))))
            addView(infoRow("UTC 时间", utcFmt.format(Date(ms))))
            addView(infoRow("星期", weekday(ms)))
        }
    }

    private fun addResultFromDate(ms: Long) {
        label("转换结果（日期 → 时间戳）")
        card {
            addView(infoRow("时间戳（秒）", (ms / 1000).toString()))
            addView(infoRow("时间戳（毫秒）", ms.toString()))
            addView(infoRow("UTC 时间", utcFmt.format(Date(ms))))
        }
    }
}
