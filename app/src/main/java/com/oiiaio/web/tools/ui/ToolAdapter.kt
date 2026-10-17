package com.oiiaio.web.tools.ui

import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oiiaio.web.tools.R
import com.oiiaio.web.tools.model.Section
import com.oiiaio.web.tools.model.Tool

class ToolAdapter(
    private val host: BaseActivity,
    private val onClick: (Tool) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private sealed class Row {
        data class Header(val section: Section) : Row()
        data class Item(val tool: Tool, val color: Int) : Row()
    }

    private val rows = mutableListOf<Row>()

    fun attachGrid(recycler: RecyclerView) {
        val glm = GridLayoutManager(host, 2)
        glm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int =
                if (rows[position] is Row.Header) 2 else 1
        }
        recycler.layoutManager = glm
        recycler.adapter = this
    }

    fun showSections(sections: List<Section>) {
        rows.clear()
        sections.forEach { s ->
            rows.add(Row.Header(s))
            val color = ContextCompat.getColor(host, s.category.color)
            s.tools.forEach { rows.add(Row.Item(it, color)) }
        }
        notifyDataSetChanged()
    }

    fun showResults(tools: List<Tool>, accent: Int) {
        rows.clear()
        tools.forEach { rows.add(Row.Item(it, accent)) }
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int =
        if (rows[position] is Row.Header) TYPE_HEADER else TYPE_ITEM

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(host)
        return if (viewType == TYPE_HEADER) {
            HeaderVH(inflater.inflate(R.layout.item_section_header, parent, false))
        } else {
            ItemVH(inflater.inflate(R.layout.item_tool_card, parent, false))
        }
    }

    override fun getItemCount() = rows.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val row = rows[position]) {
            is Row.Header -> {
                val h = holder as HeaderVH
                h.emoji.text = row.section.category.emoji
                h.name.text = row.section.category.name
                h.name.setTextColor(host.cText)
                h.count.text = row.section.tools.size.toString()
                h.count.setTextColor(host.cSub)
            }
            is Row.Item -> {
                val v = holder as ItemVH
                v.name.text = row.tool.name
                v.name.setTextColor(host.cText)
                v.desc.text = row.tool.desc
                v.desc.setTextColor(host.cSub)
                v.emoji.text = row.tool.emoji
                v.emoji.background = host.gradStroke(
                    withAlpha(row.color, 0x1F), withAlpha(row.color, 0x55), 11f
                )
                val content = host.gradStroke(host.cSurface, host.cStroke, 14f)
                v.card.background = RippleDrawable(
                    ColorStateList.valueOf(withAlpha(row.color, 0x33)), content, null
                )
                v.card.setOnClickListener { onClick(row.tool) }
            }
        }
    }

    private fun withAlpha(color: Int, alpha: Int): Int =
        (alpha shl 24) or (color and 0x00FFFFFF)

    class HeaderVH(v: View) : RecyclerView.ViewHolder(v) {
        val emoji: TextView = v.findViewById(R.id.emoji)
        val name: TextView = v.findViewById(R.id.name)
        val count: TextView = v.findViewById(R.id.count)
    }

    class ItemVH(v: View) : RecyclerView.ViewHolder(v) {
        val card: View = v.findViewById(R.id.card)
        val emoji: TextView = v.findViewById(R.id.emoji)
        val name: TextView = v.findViewById(R.id.name)
        val desc: TextView = v.findViewById(R.id.desc)
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }
}
