package com.oiiaio.web.tools.ui

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue

fun Context.dp(v: Float): Int =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v, resources.displayMetrics).toInt()

fun Context.dp(v: Int): Int = dp(v.toFloat())

fun Context.spPx(v: Float): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, v, resources.displayMetrics)

/** Rounded rectangle drawable built at runtime so colors always follow the active theme. */
fun Context.grad(
    fill: Int,
    strokeColor: Int = 0,
    strokeWidthDp: Float = 0f,
    radiusDp: Float = 12f
): GradientDrawable = GradientDrawable().apply {
    shape = GradientDrawable.RECTANGLE
    setColor(fill)
    cornerRadius = dp(radiusDp).toFloat()
    if (strokeColor != 0 && strokeWidthDp > 0f) setStroke(dp(strokeWidthDp), strokeColor)
}

fun Context.gradStroke(fill: Int, strokeColor: Int, radiusDp: Float = 12f) =
    grad(fill, strokeColor, 1f, radiusDp)
