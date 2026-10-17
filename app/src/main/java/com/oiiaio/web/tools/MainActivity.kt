package com.oiiaio.web.tools

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.oiiaio.web.tools.model.ToolRegistry
import com.oiiaio.web.tools.tools.system.AboutActivity
import com.oiiaio.web.tools.tools.system.SettingsActivity
import com.oiiaio.web.tools.ui.BaseActivity
import com.oiiaio.web.tools.ui.ToolAdapter
import com.oiiaio.web.tools.ui.gradStroke

class MainActivity : BaseActivity() {

    private lateinit var adapter: ToolAdapter
    private lateinit var recycler: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var searchBox: LinearLayout
    private lateinit var etSearch: EditText
    private lateinit var btnClear: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchBox = findViewById<EditText>(R.id.et_search).parent as LinearLayout
        searchBox.background = gradStroke(cSurface, cStroke, 12f)

        findViewById<TextView>(R.id.tv_subtitle).text =
            "开发者随身工具集 · ${ToolRegistry.all.size} 个工具"

        findViewById<ImageView>(R.id.btn_about).setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
        findViewById<ImageView>(R.id.btn_settings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        recycler = findViewById(R.id.recycler)
        adapter = ToolAdapter(this) { tool ->
            startActivity(Intent(this, tool.target))
        }
        adapter.attachGrid(recycler)
        adapter.showSections(ToolRegistry.sections)

        // empty-state view, same weight as the list
        emptyView = TextView(this).apply {
            visibility = View.GONE
            gravity = Gravity.CENTER
            textSize = 14f
            setTextColor(cSub)
            typeface = Typeface.DEFAULT
            setPadding(0, dp(80f), 0, 0)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
            )
        }
        (recycler.parent as ViewGroup).addView(emptyView)

        etSearch = findViewById(R.id.et_search)
        btnClear = findViewById(R.id.btn_clear)
        btnClear.setOnClickListener { etSearch.setText("") }
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun afterTextChanged(s: Editable?) = filter(s?.toString().orEmpty())
        })
    }

    private fun filter(queryRaw: String) {
        val q = queryRaw.trim()
        btnClear.visibility = if (q.isEmpty()) View.GONE else View.VISIBLE
        if (q.isEmpty()) {
            recycler.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
            adapter.showSections(ToolRegistry.sections)
            return
        }
        val matched = ToolRegistry.all.filter {
            it.name.contains(q, true) || it.desc.contains(q, true) || it.emoji.contains(q)
        }
        if (matched.isEmpty()) {
            recycler.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
            emptyView.text = "未找到与「$q」相关的工具"
        } else {
            recycler.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
            adapter.showResults(matched, cPrimary)
        }
    }
}
