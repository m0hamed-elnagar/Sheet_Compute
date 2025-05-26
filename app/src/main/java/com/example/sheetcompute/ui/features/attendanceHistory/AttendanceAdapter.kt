package com.example.sheetcompute.ui.features.attendanceHistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sheetcompute.R
import com.example.sheetcompute.data.local.entities.AttendanceRecordUI
import com.example.sheetcompute.databinding.AttendanceItemBinding
import com.example.sheetcompute.ui.subFeatures.utils.DateUtils.getMonthName

class AttendanceAdapter(
    private val lifecycleScope: LifecycleCoroutineScope,
    private val onSelected: (Int) -> Unit
) : PagingDataAdapter<AttendanceRecordUI, AttendanceAdapter.ViewHolder>(AttendanceDiffCallback()) {
   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AttendanceItemBinding.inflate(
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
        private val binding: AttendanceItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(basics: AttendanceRecordUI) {
            with(binding) {
                txtId.text = basics.id.toString()
                txtName.text = basics.name
                txttardyCount.text = basics.tardyCount.toString()
                txtabsentsCount.text = basics.absentCount.toString()
                txtMonth.text =
                    txtMonth.context.getString(R.string.date, getMonthName(basics.month), basics.year.toString())
                txtWorkingDaysCount.text = basics.workingDays.toString()

                    root.setOnClickListener {
                        onSelected(basics.id)
                    }

            }
        }
    }
}

private class AttendanceDiffCallback : DiffUtil.ItemCallback<AttendanceRecordUI>() {
    override fun areItemsTheSame(oldItem: AttendanceRecordUI, newItem: AttendanceRecordUI): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: AttendanceRecordUI, newItem: AttendanceRecordUI): Boolean =
        oldItem == newItem
    }
