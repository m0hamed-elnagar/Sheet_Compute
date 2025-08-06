package com.example.sheetcompute.ui.features.attendanceHistory.searchHistory


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sheetcompute.data.entities.EmployeeEntity
import com.example.sheetcompute.databinding.SearchItemBinding

class SearchEmployeeAdapter(
    private val onSelected: (Long) -> Unit
) : RecyclerView.Adapter<SearchEmployeeAdapter.ViewHolder>() {
    private val asyncDiffer = AsyncListDiffer(this, EmployeeDiffUtilCallback())
    fun submitList(list: List<EmployeeEntity>) {
        asyncDiffer.submitList(list)
    }

    override fun getItemCount(): Int = asyncDiffer.currentList.size
    override fun getItemId(position: Int): Long =
        asyncDiffer.currentList[position].id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SearchItemBinding.inflate(
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
        private val binding: SearchItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: EmployeeEntity) {
            with(binding) {
                txtName.text = item.name
                txtId.text = item.id.toString()
                root.setOnClickListener {
                    onSelected(item.id)
                }
            }
        }
    }
}


private class EmployeeDiffUtilCallback : DiffUtil.ItemCallback<EmployeeEntity>() {
    override fun areItemsTheSame(oldItem: EmployeeEntity, newItem: EmployeeEntity): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: EmployeeEntity, newItem: EmployeeEntity): Boolean =
        oldItem == newItem
}