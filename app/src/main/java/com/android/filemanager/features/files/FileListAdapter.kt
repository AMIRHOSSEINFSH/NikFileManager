package com.android.filemanager.features.files

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.filemanager.R
import com.android.filemanager.databinding.ItemFileGridBinding
import com.android.filemanager.databinding.ItemFileLinearBinding
import com.google.android.material.card.MaterialCardView
import java.io.File

class FileListAdapter(
    @LayoutRes private var itemViewType: Int = R.layout.item_file_linear,
    private val itemClicked: (File) -> Boolean,
    private val itemLongClicked: (List<File>) -> Unit
) :
    ListAdapter<File, RecyclerView.ViewHolder>(object :
        DiffUtil.ItemCallback<File>() {
        override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }) {

    private val selectedList = HashMap<Int, File?>()

    init {
        for (i in 0..currentList.size) {
            selectedList[i] = null
        }
    }

    inner class GridViewHolder(private val binding: ItemFileGridBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private fun longSelect(it: View) {
            it as MaterialCardView
            if (it.isChecked) {
                it.isChecked = false
                selectedList[bindingAdapterPosition] = null
            } else {
                it.isChecked = true
                selectedList[bindingAdapterPosition] = currentList[bindingAdapterPosition]
            }

            itemLongClicked.invoke(selectedList.mapNotNull { it.value })
        }

        init {
            binding.materialContainer.setOnClickListener {
                if (!itemClicked.invoke(currentList[bindingAdapterPosition])) {
                    longSelect(it)
                }

            }
            binding.materialContainer.setOnLongClickListener { view ->
                longSelect(view)
                true
            }
        }

        fun bind(file: File) {
            if (binding.materialContainer.isChecked && selectedList[bindingAdapterPosition] == null) {
                binding.materialContainer.isChecked = false
            } else if (!binding.materialContainer.isChecked && selectedList[bindingAdapterPosition] != null) {
                binding.materialContainer.isChecked = true
            }
            binding.file = file
            binding.txtName.isSelected = true
        }
    }

    inner class LinearViewHolder(private val binding: ItemFileLinearBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private fun longSelect(it: View) {
            it as MaterialCardView
            if (it.isChecked) {
                it.isChecked = false
                selectedList[bindingAdapterPosition] = null
            } else {
                it.isChecked = true
                selectedList[bindingAdapterPosition] = currentList[bindingAdapterPosition]
            }

            itemLongClicked.invoke(selectedList.mapNotNull { it.value })
        }

        init {
            binding.materialContainer.setOnClickListener {
                if (!itemClicked.invoke(currentList[bindingAdapterPosition])) {
                    longSelect(it)
                }

            }
            binding.materialContainer.setOnLongClickListener { it ->
                longSelect(it)
                true
            }
        }

        fun bind(file: File) {
            if (binding.materialContainer.isChecked && selectedList[bindingAdapterPosition] == null) {
                binding.materialContainer.isChecked = false
            } else if (!binding.materialContainer.isChecked && selectedList[bindingAdapterPosition] != null) {
                binding.materialContainer.isChecked = true
            }
            binding.file = file
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (itemViewType == R.layout.item_file_grid) {
            val binding =
                ItemFileGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            GridViewHolder(binding)
        } else {
            val binding =
                ItemFileLinearBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            LinearViewHolder(binding)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is GridViewHolder -> {
                holder.bind(getItem(position))
            }

            is LinearViewHolder -> {
                holder.bind(getItem(position))
            }
        }
    }

    fun clearSelectedList() {
        selectedList.clear()
        notifyItemRangeChanged(0, itemCount)
    }

    override fun getItemViewType(position: Int): Int {
        return itemViewType
    }

    fun setItemViewType(@LayoutRes itemViewType: Int) {
        this.itemViewType = itemViewType
        notifyItemRangeChanged(0, itemCount - 1)
    }

    fun clearSelection() {
        selectedList.clear()
        notifyItemRangeChanged(0, itemCount - 1)
    }

    fun selectAll() {
        currentList.forEachIndexed { index, file ->
            selectedList[index] = file
        }
        notifyItemRangeChanged(0, itemCount - 1)
    }
}