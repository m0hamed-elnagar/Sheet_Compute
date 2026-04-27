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
import com.example.sheetcompute.ui.subFeatures.utils.DateUtils
import java.time.format.DateTimeFormatter
import java.util.Locale

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
                val date = item.date
                tvMonth.text = date.format(DateTimeFormatter.ofPattern("MMM")).uppercase(Locale.ROOT)
                tvDayNum.text = date.format(DateTimeFormatter.ofPattern("dd"))
                tvYear.text = date.format(DateTimeFormatter.ofPattern("yyyy"))
                tvDay.text = date.format(DateTimeFormatter.ofPattern("EEEE"))

                // Status
                tvStatus.text = item.status.name
                when (item.status) {
                    AttendanceStatus.PRESENT -> {
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.dark_status_present))
                        tvAdditionalInfo.text = "Checked in at ${item.loginTime}"
                        tvAdditionalInfo.visibility = View.VISIBLE
                    }
                    AttendanceStatus.ABSENT -> {
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.dark_status_absent))
                        tvAdditionalInfo.text = "No check-in record"
                        tvAdditionalInfo.visibility = View.VISIBLE
                    }
                    AttendanceStatus.LATE -> {
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.dark_status_late))
                        val formattedLate = DateUtils.formatMinutesToHoursMinutes(item.lateDuration)
                        tvAdditionalInfo.text = "Late by $formattedLate (In: ${item.loginTime})"
                        tvAdditionalInfo.visibility = View.VISIBLE
                    }
                    AttendanceStatus.EXTRA_DAY -> {
                        tvStatus.setTextColor(ContextCompat.getColor(context, R.color.dark_status_extra))
                        tvAdditionalInfo.text = "Checked in at ${item.loginTime}"
                        tvAdditionalInfo.visibility = View.VISIBLE
                    }
                }

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
