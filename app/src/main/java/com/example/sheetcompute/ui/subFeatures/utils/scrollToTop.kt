package com.example.sheetcompute.ui.subFeatures.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.scrollToTop() {
    val manager = layoutManager as? LinearLayoutManager ?: return
    manager.scrollToPositionWithOffset(0, 0)
}
