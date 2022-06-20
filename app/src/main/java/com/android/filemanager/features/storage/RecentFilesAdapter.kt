package com.android.filemanager.features.storage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.filemanager.databinding.ItemFileGridBinding
import java.io.File

class RecentFilesAdapter(private val itemClicked: (File) -> Unit ) : ListAdapter<File, RecentFilesAdapter.RecentViewHolder>(object :
    DiffUtil.ItemCallback<File>() {
    override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }

    override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }

}) {
    inner class RecentViewHolder(private val binding: ItemFileGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                itemClicked.invoke(currentList[bindingAdapterPosition])
            }
        }

        fun bind(position: Int) {
            binding.file = currentList[position]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentViewHolder {
        val binding =
            ItemFileGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecentViewHolder, position: Int) {
        holder as RecentViewHolder
        holder.bind(position)
    }
}