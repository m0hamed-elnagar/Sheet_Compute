package com.example.sheetcompute.ui.features.employeeAttendance

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sheetcompute.R
import com.example.sheetcompute.data.entities.AttendanceStatus
import com.example.sheetcompute.data.entities.EmployeeAttendanceRecord
import com.example.sheetcompute.databinding.EmployeeItem2Binding
import com.example.sheetcompute.ui.subFeatures.utils.DateUtils.formatMinutesToHoursMinutes
import java.time.format.DateTimeFormatter

class EmployeeAttendanceAdapter(
    private val context: Context,
    private val onSelected: (Long) -> Unit
) : RecyclerView.Adapter< EmployeeAttendanceAdapter.ViewHolder>(
) {
    private val asyncDiffer = AsyncListDiffer(this, AttendanceDiffCallback())
    fun submitList(list: List<EmployeeAttendanceRecord>) {
        asyncDiffer.submitList(list)
    }
    override fun getItemCount(): Int = asyncDiffer.currentList.size
    override fun getItemId(position: Int): Long =
        asyncDiffer.currentList[position].id


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = EmployeeItem2Binding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(asyncDiffer.currentList[position])
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

                val lateDuration = item.lateDuration ?: 0
                if (lateDuration > 0) {
                    tvLateDuration.text = formatMinutesToHoursMinutes(lateDuration)
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
                        binding.ivLateIcon.visibility = View.VISIBLE
                        binding.tvLateDuration.visibility = View.VISIBLE
                        binding.tvAdditionalInfo.visibility = View.VISIBLE
                    }
                    AttendanceStatus.ABSENT -> {
                        chipStatus.chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.status_absent)
                        binding.ivLateIcon.visibility = View.GONE // Hide late icon for absent
                        binding.tvLateDuration.visibility = View.GONE // Hide late duration for absent
                        binding.tvAdditionalInfo.visibility = View.GONE // Hide additional info for absent
                    }
                    AttendanceStatus.LATE -> {
                        chipStatus.chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.status_late)
                        binding.ivLateIcon.visibility = View.VISIBLE
                        binding.tvLateDuration.visibility = View.VISIBLE
                        binding.tvAdditionalInfo.visibility = View.VISIBLE
                    }
                    AttendanceStatus.EXTRA_DAY -> {
                        chipStatus.chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.status_present)
                        binding.ivLateIcon.visibility = View.VISIBLE
                        binding.tvLateDuration.visibility = View.VISIBLE
                        binding.tvAdditionalInfo.visibility = View.VISIBLE
                    }
                }

                // Bind additional info
                tvAdditionalInfo.text = "Checked in at ${item.loginTime}"
                tvAdditionalInfo.visibility = View.VISIBLE

                // Handle click if needed
                root.setOnClickListener {
                    onSelected(item.id)
                }
            }
        }
    }
}

private class AttendanceDiffCallback : DiffUtil.ItemCallback<EmployeeAttendanceRecord>() {
    override fun areItemsTheSame(oldItem: EmployeeAttendanceRecord, newItem: EmployeeAttendanceRecord): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: EmployeeAttendanceRecord, newItem: EmployeeAttendanceRecord): Boolean =
        oldItem == newItem
}
