package com.example.sheetcompute.ui.features.employeeAttendance

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sheetcompute.R
import com.example.sheetcompute.data.local.entities.AttendanceStatus
import com.example.sheetcompute.data.local.entities.EmployeeAttendanceRecord
import com.example.sheetcompute.databinding.EmployeeItem2Binding
import com.example.sheetcompute.ui.subFeatures.utils.DateUtils
import java.time.format.DateTimeFormatter

class EmployeeAttendanceAdapter(
    private val context: Context,
    private val onSelected: (Int) -> Unit
) : PagingDataAdapter<EmployeeAttendanceRecord, EmployeeAttendanceAdapter.ViewHolder>(
    AttendanceDiffCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = EmployeeItem2Binding.inflate(
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
        private val binding: EmployeeItem2Binding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: EmployeeAttendanceRecord) {
            with(binding) {
                // Bind date information
                val date = item.date
                tvDate.text = date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) // e.g. "May 15, 2023"
                tvDay.text = date.format(DateTimeFormatter.ofPattern("EEEE")) // e.g. "Monday"

                // Bind late duration
                val loginTimeMinutes = 540 // 9:00 AM in minutes
                val lateDuration = item.lateDuration ?: 0
                if (lateDuration > 0) {
                    tvLateDuration.text = "$lateDuration mins late"
                    tvLateDuration.setTextColor(ContextCompat.getColor(context, R.color.late_red)) // make sure R.color.red is defined
                } else {
                    tvLateDuration.text = "On time"
                    tvLateDuration.setTextColor(ContextCompat.getColor(context, R.color.status_present)) // make sure R.color.green is defined
                }


                // Bind status
                chipStatus.text = item.status.name
                when (item.status) {
                    AttendanceStatus.PRESENT -> {
                        chipStatus.chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.status_present)
                    }
                    AttendanceStatus.ABSENT -> {
                        chipStatus.chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.status_absent)
                    }
                    AttendanceStatus.LATE -> {
                        chipStatus.chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.status_late)
                    }
                    AttendanceStatus.EXTRA_DAY -> {
                        chipStatus.chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.status_present)
                    }
                }

                // Bind additional info
                tvAdditionalInfo.text = "Checked in at ${DateUtils.minutesToTimeString(item.loginTime)}"
                tvAdditionalInfo.visibility = View.VISIBLE

                // Handle click if needed
                root.setOnClickListener {
                    onSelected(item.Id)
                }
            }
        }
    }
}

private class AttendanceDiffCallback : DiffUtil.ItemCallback<EmployeeAttendanceRecord>() {
    override fun areItemsTheSame(oldItem: EmployeeAttendanceRecord, newItem: EmployeeAttendanceRecord): Boolean =
        oldItem.Id == newItem.Id

    override fun areContentsTheSame(oldItem: EmployeeAttendanceRecord, newItem: EmployeeAttendanceRecord): Boolean =
        oldItem == newItem
}
