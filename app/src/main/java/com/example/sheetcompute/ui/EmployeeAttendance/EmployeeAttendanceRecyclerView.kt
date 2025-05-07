package com.example.sheetcompute.ui.EmployeeAttendance

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sheetcompute.R
import com.example.sheetcompute.data.roomDB.entities.AttendanceItem
import com.example.sheetcompute.databinding.EmployeeItemBinding
import com.example.sheetcompute.ui.utils.DateUtils.format
import java.text.SimpleDateFormat
import java.util.Locale

class EmployeeAttendanceRecyclerView( private val context: Context,private val onSelected: (Int) -> Unit) :
    PagingDataAdapter<AttendanceItem, EmployeeAttendanceRecyclerView.ViewHolder>(
        AttendanceDiffCallback()
    ) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = EmployeeItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

inner class ViewHolder(
    private val binding: EmployeeItemBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: AttendanceItem) {
        with(binding) {
            // Bind date information
            item.logInTime?.let { date ->
                tvDate.text = date.format("dd MMM") // e.g. "12 May"
                tvDay.text = SimpleDateFormat("EEEE", Locale.getDefault()).format(date) // e.g. "Monday"
                tvYear.text = date.format("yyyy") // e.g. "2025"
            }

            // Bind late information
            tvLateTime.text = item.lateDuration ?: "0 min"
            tvArrivalTime.text = if (item.logInTime != null) {
                "Arrived at ${item.logInTime.format("h:mm a")}" // e.g. "8:45 AM"
            } else {
                "No check-in recorded"
            }

            // Bind status
            tvStatus.text = item.status
            when (item.status.lowercase(Locale.getDefault())) {
                "present" -> {
                    statusIndicator.setCardBackgroundColor(ContextCompat.getColor(context, R.color.statusPresent))
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.statusPresent))
                }
                "absent" -> {
                    statusIndicator.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.late_red))
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.late_red))
                }
                "late" -> {
                    statusIndicator.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.colorSecondaryDark))
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.colorSecondaryDark))
                }
                "halfday" -> {
                    statusIndicator.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.purple_700))
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.purple_700))
                }
                else -> {
                    statusIndicator.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.textSecondary))
                    tvStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.textSecondary))
                }
            }

            // Handle click if needed
            root.setOnClickListener {
                // Handle item click
            }
        }
    }
}}

private class AttendanceDiffCallback : DiffUtil.ItemCallback<AttendanceItem>() {
    override fun areItemsTheSame(oldItem: AttendanceItem, newItem: AttendanceItem): Boolean =
        oldItem.Id == newItem.Id

    override fun areContentsTheSame(oldItem: AttendanceItem, newItem: AttendanceItem): Boolean =
        oldItem == newItem
}
