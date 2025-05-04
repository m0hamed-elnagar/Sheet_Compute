package com.example.sheetcompute.ui.attendanceHistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sheetcompute.data.roomDB.entities.AttendanceRecordUI
import com.example.sheetcompute.databinding.AttendanceItemBinding
import com.example.sheetcompute.ui.utils.DateUtils.getMonthName
import kotlinx.coroutines.launch

class AttendanceRecyclerView(
    private val lifecycleScope: LifecycleCoroutineScope,
    private val onSelected: (Int) -> Unit
) : PagingDataAdapter<AttendanceRecordUI, AttendanceRecyclerView.ViewHolder>(AttendanceDiffCallback()) {
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
                textNotificationPosition.text  = absoluteAdapterPosition.toString()
                txtId.text = basics.id.toString()
                txtName.text = basics.name
                txttardyCount.text = basics.tardyCount.toString()
                txtabsentsCount.text = basics.absentCount.toString()
                txtMonth.text = getMonthName(basics.month)
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
