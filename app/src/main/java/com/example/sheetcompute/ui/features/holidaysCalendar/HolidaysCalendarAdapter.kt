package com.example.sheetcompute.ui.features.holidaysCalendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sheetcompute.data.entities.Holiday
import com.example.sheetcompute.databinding.ItemHolidayBinding
import java.time.format.DateTimeFormatter

class HolidayAdapter(
    private val onDeleteClick: (Holiday) -> Unit,
    private val onEditClick: ((Holiday) -> Unit)? = null
) : ListAdapter<Holiday, HolidayAdapter.HolidayViewHolder>(HolidayDiffCallback()) {

    private val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolidayViewHolder {
        val binding = ItemHolidayBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HolidayViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HolidayViewHolder, position: Int) {
        val holiday = getItem(position)
        holder.bind(holiday)
    }

    inner class HolidayViewHolder(
        private val binding: ItemHolidayBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(holiday: Holiday) {
            with(binding) {
                tvHolidayName.text = holiday.name

                // todo make the format utill and use the same format everywhere Format date display
                tvHolidayDate.text = if (holiday.startDate == holiday.endDate) {
                    holiday.startDate.format(dateFormatter)
                } else {
                    "${holiday.startDate.format(dateFormatter)} - ${holiday.endDate.format(dateFormatter)}"
                }

                // Handle note visibility
                if (holiday.note.isNotBlank()) {
                    tvHolidayNote.text = holiday.note
                    tvHolidayNote.visibility = android.view.View.VISIBLE
                } else {
                    tvHolidayNote.visibility = android.view.View.GONE
                }

                // Set up click listeners
                btnDeleteHoliday.setOnClickListener {
                    onDeleteClick(holiday)
                }

                // Optional edit functionality
                onEditClick?.let { editCallback ->
                    root.setOnClickListener {
                        editCallback(holiday)
                    }
                }
            }
        }
    }

    private class HolidayDiffCallback : DiffUtil.ItemCallback<Holiday>() {
        override fun areItemsTheSame(oldItem: Holiday, newItem: Holiday): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Holiday, newItem: Holiday): Boolean {
            return oldItem == newItem
        }
    }
}

// Extension function for better date range formatting
private fun formatDateRange(startDate: java.time.LocalDate, endDate: java.time.LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")

    return when {
        startDate == endDate -> startDate.format(formatter)
        startDate.year == endDate.year && startDate.month == endDate.month -> {
            // Same month: "Jan 15 - 20, 2024"
            "${startDate.format(DateTimeFormatter.ofPattern("MMM dd"))} - ${endDate.format(formatter)}"
        }
        startDate.year == endDate.year -> {
            // Same year: "Jan 15 - Feb 20, 2024"
            "${startDate.format(DateTimeFormatter.ofPattern("MMM dd"))} - ${endDate.format(formatter)}"
        }
        else -> {
            // Different years: "Dec 25, 2023 - Jan 5, 2024"
            "${startDate.format(formatter)} - ${endDate.format(formatter)}"
        }
    }
}