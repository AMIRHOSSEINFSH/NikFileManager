package com.android.filemanager.components

import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import coil.load
import com.android.filemanager.R
import com.android.filemanager.core.StorageHelper
import java.io.File


@BindingAdapter("app:setNumberOfFiles")
fun setFilesOfFolder(textView: AppCompatTextView, file: File) {
    if (!file.isFile) {
        val result = "${file.listFiles()?.size ?: 0} items"
        textView.text = result
    }
}

@BindingAdapter("app:compressFileName")
fun setName(textView: AppCompatTextView, name: String) {
    val result = if (name.length > 15) {
        StorageHelper.compressName(name)
    } else name
    textView.text = result
}

@BindingAdapter("app:setIconDrawable")
fun setIcon(imageView: AppCompatImageView, file: File) {
    if (file.isFile) {
        val drawableId = StorageHelper.getDrawableIcon(file)
        imageView.load(drawableId)
    } else imageView.load(R.drawable.ic_folder)
}

