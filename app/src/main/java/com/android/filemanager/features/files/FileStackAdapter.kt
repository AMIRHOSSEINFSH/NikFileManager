package com.android.filemanager.features.files

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.filemanager.R
import com.android.filemanager.data.model.dataClass.StackFolder
import com.android.filemanager.databinding.StackFolderItemBinding
import com.google.android.material.textview.MaterialTextView

class FileStackAdapter constructor(
    private val itemClick: (StackFolder) -> Unit
) : ListAdapter<StackFolder, FileStackAdapter.StackViewHolder>(
    object : DiffUtil.ItemCallback<StackFolder>() {
        override fun areItemsTheSame(oldItem: StackFolder, newItem: StackFolder): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: StackFolder, newItem: StackFolder): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }
) {

    inner class StackViewHolder(private val binding: StackFolderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                itemClick.invoke(currentList[bindingAdapterPosition])
            }
        }

        fun bind(item: StackFolder) {
            if (bindingAdapterPosition == itemCount -1) {
                (binding.root as MaterialTextView).setTextColor(binding.root.context.resources.getColor(
                    R.color.black))
            }else{
                (binding.root as MaterialTextView).setTextColor(binding.root.context.resources.getColor(
                    R.color.unUsed))
            }
            binding.item = item.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StackViewHolder {
        val binding =
            StackFolderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StackViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}