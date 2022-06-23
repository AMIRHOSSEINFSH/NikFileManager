package com.android.filemanager.features.highProcess

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

class FileListNavigateAdapter(
    @LayoutRes private var itemViewType: Int = R.layout.item_file_linear,
    private val itemClicked: (File) -> Unit
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

    inner class GridViewHolder(private val binding: ItemFileGridBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.materialContainer.setOnClickListener {
                itemClicked.invoke(currentList[bindingAdapterPosition])
            }
        }

        fun bind(file: File) {
            binding.file = file
        }
    }

    inner class LinearViewHolder(private val binding: ItemFileLinearBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.materialContainer.setOnClickListener {
                itemClicked.invoke(currentList[bindingAdapterPosition])
            }
        }

        fun bind(file: File) {
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

    override fun getItemViewType(position: Int): Int {
        return itemViewType
    }

    fun setItemViewType(@LayoutRes itemViewType: Int) {
        this.itemViewType = itemViewType
        notifyItemRangeChanged(0, itemCount - 1)
    }


}