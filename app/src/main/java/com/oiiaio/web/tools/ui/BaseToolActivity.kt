package com.oiiaio.web.tools.ui

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.materialswitch.MaterialSwitch
import com.oiiaio.web.tools.R

/**
 * Base class for every single tool screen. The whole UI is built in code so that each
 * feature stays a small, self-contained file while colors follow the active theme.
 */
abstract class BaseToolActivity : BaseActivity() {

    protected lateinit var content: LinearLayout
    protected lateinit var toolbar: MaterialToolbar

    protected abstract fun toolTitle(): String
    protected open fun toolSubtitle(): String? = null
    protected abstract fun onBuild()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tool)
        toolbar = findViewById(R.id.toolbar)
        toolbar.title = toolTitle()
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener { finish() }
        toolbar.setTitleTextColor(cText)
        content = findViewById(R.id.content)
        onBuild()
    }

    protected fun addView(v: View): View {
        content.addView(v)
        return v
    }

    // ---- spacing / labels ----
    protected fun gap(heightDp: Int = 10) {
        addView(View(this).apply {
            layoutParams = LinearLayout.LayoutParams(1, dp(heightDp))
        })
    }

    protected fun labelText(text: String): TextView =
        TextView(this).apply {
            this.text = text
            setTextColor(cSub)
            textSize = 12.5f
            letterSpacing = 0.04f
            setPadding(dp(2f), 0, 0, dp(6f))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { topMargin = dp(12f) }
        }

    protected fun label(text: String, marginTop: Int = 12): TextView {
        val tv = labelText(text)
        (tv.layoutParams as LinearLayout.LayoutParams).topMargin = dp(marginTop.toFloat())
        addView(tv)
        return tv
    }

    protected fun note(text: String): TextView {
        val tv = TextView(this).apply {
            this.text = text
            setTextColor(cSub)
            textSize = 12f
            setPadding(dp(2f), dp(6f), dp(2f), 0)
        }
        addView(tv)
        return tv
    }

    // ---- card container ----
    protected fun card(init: LinearLayout.() -> Unit): LinearLayout {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = gradStroke(cSurface, cStroke, 16f)
            setPadding(dp(14f), dp(12f), dp(14f), dp(14f))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(12f) }
        }
        card.init()
        addView(card)
        return card
    }

    // ---- inputs / outputs ----
    protected fun input(
        hint: String,
        multi: Boolean = false,
        mono: Boolean = false,
        minLines: Int = 1,
        numeric: Boolean = false
    ): EditText {
        val et = EditText(this).apply {
            setHint(hint)
            setTextColor(cText)
            setHintTextColor(cSub)
            textSize = 14f
            setPadding(dp(13f), dp(11f), dp(13f), dp(11f))
            background = gradStroke(cSurface2, cStroke, 12f)
            if (mono) typeface = Typeface.MONOSPACE
            if (multi) {
                inputType = InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                    InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                gravity = Gravity.TOP or Gravity.START
                setMinLines(minLines)
                setLineSpacing(dp(2f).toFloat(), 1f)
                isVerticalScrollBarEnabled = true
            } else {
                inputType = if (numeric)
                    InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                isSingleLine = !multi
            }
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(10f) }
        }
        return et
    }

    protected fun output(hint: String = "结果输出", minLines: Int = 3): EditText {
        val et = EditText(this).apply {
            setHint(hint)
            setTextColor(cText)
            setHintTextColor(cSub)
            textSize = 14f
            setPadding(dp(13f), dp(11f), dp(13f), dp(11f))
            background = grad(cSurface2, cStroke, 1f, 12f)
            typeface = Typeface.MONOSPACE
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            gravity = Gravity.TOP or Gravity.START
            setMinLines(minLines)
            setLineSpacing(dp(2f).toFloat(), 1f)
            isVerticalScrollBarEnabled = true
            setTextIsSelectable(true)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(8f) }
        }
        return et
    }

    // ---- buttons ----
    private fun baseButton(
        text: String,
        filled: Boolean,
        onClick: () -> Unit
    ): MaterialButton {
        val btn = MaterialButton(this).apply {
            this.text = text
            cornerRadius = dp(11f)
            stateListAnimator = null
            insetTop = 0
            insetBottom = 0
            textSize = 14f
            this.layoutParams = LinearLayout.LayoutParams(0, dp(44f), 1f).apply {
                marginEnd = dp(6f)
            }
            setOnClickListener { onClick() }
        }
        return if (filled) {
            btn.apply {
                backgroundTintList = android.content.res.ColorStateList.valueOf(cPrimary)
                setTextColor(Color.WHITE)
                iconPadding = dp(6f)
            }
        } else {
            btn.apply {
                backgroundTintList = android.content.res.ColorStateList.valueOf(Color.TRANSPARENT)
                strokeColor = android.content.res.ColorStateList.valueOf(cPrimary)
                strokeWidth = dp(1.2f)
                setTextColor(cPrimary)
            }
        }
    }

    protected fun primaryBtn(text: String, onClick: () -> Unit) = baseButton(text, true, onClick)
    protected fun ghostBtn(text: String, onClick: () -> Unit) = baseButton(text, false, onClick)

    /** Horizontal row whose children share width equally. */
    protected fun row(vararg views: View, gap: Int = 8): LinearLayout {
        val ll = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(10f) }
        }
        views.forEach { v ->
            if (v.layoutParams == null) {
                v.layoutParams = LinearLayout.LayoutParams(0, dp(44f), 1f)
            }
            val lp = LinearLayout.LayoutParams(0, v.layoutParams.height, 1f).apply {
                marginEnd = dp(gap.toFloat())
            }
            v.layoutParams = lp
            ll.addView(v)
        }
        return ll
    }

    // ---- segmented toggle (single selection) ----
    protected fun segmented(
        options: List<String>,
        initial: Int = 0,
        onSelect: (Int, String) -> Unit
    ): MaterialButtonToggleGroup {
        val group = MaterialButtonToggleGroup(this).apply {
            isSingleSelection = true
            isSelectionRequired = true
            setBackgroundColor(Color.TRANSPARENT)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(10f) }
        }
        options.forEachIndexed { i, opt ->
            val b = MaterialButton(this).apply {
                text = opt
                id = 1000 + i
                cornerRadius = dp(9f)
                insetTop = 0
                insetBottom = 0
                textSize = 12.5f
                setTextColor(cText)
                isCheckable = true
                strokeWidth = dp(1f)
                strokeColor = android.content.res.ColorStateList.valueOf(cStroke)
                backgroundTintList = android.content.res.ColorStateList.valueOf(cSurface2)
            }
            b.addOnCheckedChangeListener { button, checked ->
                if (checked) {
                    button.setBackgroundColor(cPrimary)
                    button.setTextColor(Color.WHITE)
                    onSelect(i, opt)
                } else {
                    button.setBackgroundColor(cSurface2)
                    button.setTextColor(cText)
                }
            }
            group.addView(b)
        }
        group.check(1000 + initial)
        return group
    }

    // ---- spinner ----
    protected fun dropdown(
        options: List<String>,
        initial: Int = 0,
        onSelect: (Int, String) -> Unit
    ): Spinner {
        val spinner = Spinner(this)
        val adapter = object : ArrayAdapter<String>(this, 0, options) {
            override fun getView(pos: Int, convertView: View?, parent: ViewGroup): View {
                val tv = (convertView as? TextView) ?: TextView(this@BaseToolActivity)
                tv.text = getItem(pos)
                tv.setTextColor(cText)
                tv.textSize = 14f
                tv.setPadding(dp(12f), dp(10f), dp(12f), dp(10f))
                tv.background = gradStroke(cSurface2, cStroke, 10f)
                return tv
            }

            override fun getDropDownView(pos: Int, convertView: View?, parent: ViewGroup): View {
                val tv = (convertView as? TextView) ?: TextView(this@BaseToolActivity)
                tv.text = getItem(pos)
                tv.setTextColor(cText)
                tv.textSize = 14f
                tv.setPadding(dp(14f), dp(12f), dp(14f), dp(12f))
                tv.setBackgroundColor(cSurface)
                return tv
            }
        }
        spinner.adapter = adapter
        spinner.setSelection(initial)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                onSelect(pos, options[pos])
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }
        spinner.background = null
        spinner.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = dp(10f) }
        return spinner
    }

    // ---- switch ----
    protected fun switchRow(labelText: String, initial: Boolean = false, onChange: (Boolean) -> Unit): MaterialSwitch {
        val sw = MaterialSwitch(this).apply {
            text = labelText
            setTextColor(cText)
            textSize = 14f
            isChecked = initial
            trackDecorationTintList = android.content.res.ColorStateList.valueOf(cPrimary)
            thumbTintList = android.content.res.ColorStateList.valueOf(Color.WHITE)
            setOnCheckedChangeListener { _, checked -> onChange(checked) }
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = dp(10f) }
        }
        return sw
    }

    // ---- copy / share actions for an output box ----
    protected fun resultActions(et: EditText, shareable: Boolean = true): LinearLayout {
        val copy = ghostBtn("复制结果") { if (et.text.isNotEmpty()) copy(et.text.toString()) }
        copy.layoutParams = LinearLayout.LayoutParams(0, dp(42f), 1f)
        val r = row(copy)
        if (shareable) {
            val share = ghostBtn("分享") { if (et.text.isNotEmpty()) share(et.text.toString()) }
            share.layoutParams = LinearLayout.LayoutParams(0, dp(42f), 1f)
            r.addView(share)
        }
        val clear = ghostBtn("清空") { et.setText("") }
        clear.layoutParams = LinearLayout.LayoutParams(0, dp(42f), 1f)
        r.addView(clear)
        return r
    }

    // ---- key / value info row ----
    protected fun infoRow(key: String, value: String, selectable: Boolean = true): LinearLayout {
        val ll = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(9f), 0, dp(9f))
        }
        val k = TextView(this).apply {
            text = key
            setTextColor(cSub)
            textSize = 13f
        }
        val v = TextView(this).apply {
            text = value
            setTextColor(cText)
            textSize = 13f
            gravity = Gravity.END
            if (selectable) setTextIsSelectable(true)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }
        ll.addView(k)
        ll.addView(v)
        return ll
    }
}
