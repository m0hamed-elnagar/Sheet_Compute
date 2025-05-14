package com.example.sheetcompute.ui.subFeatures.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

 fun RecyclerView.scrollToTop() {
    layoutManager?.let {
     if (layoutManager == null) {
         layoutManager = LinearLayoutManager(context)
     }

     // Immediate scroll
     scrollToPosition(0)

     // Smooth scroll after layout
     post {
         smoothScrollToPosition(0)
         (layoutManager as? LinearLayoutManager)?.scrollToPositionWithOffset(0, 0)
     }

     // Fallback check
     postDelayed({
         if ((layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition() ?: 0 > 0) {
             scrollToPosition(0)
         }
     }, 300)}
}