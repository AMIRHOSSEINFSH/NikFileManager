package com.android.filemanager.components

import android.net.Uri
import android.util.Log
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.documentfile.provider.DocumentFile
import coil.ImageLoader
import coil.load
import coil.transform.CircleCropTransformation
import com.android.filemanager.R
import com.android.filemanager.core.StorageHelper
import com.cops.iitbhu.previewer.lib.Previewer
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.*
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

@BindingAdapter("app:setStackName")
fun setStackNameFolder(tv: MaterialTextView,name: String) {
    tv.text = "<$name"
}

@BindingAdapter("app:setIconDrawable")
fun setIcon(imageView: AppCompatImageView, file: File) {
    val job = Job()
    if (file.isFile) {
        when (StorageHelper.getFormatType(file)) {
            StorageHelper.Format.ZIP -> imageView.load(StorageHelper.getDrawableIcon(file))
            StorageHelper.Format.PDF -> {
                    CoroutineScope(Dispatchers.IO + job).launch {
                        try {
                            val bitmap = Previewer.generateBitmapFromPdf(file.toUri())
                            imageView.load(bitmap) {
                                placeholder(StorageHelper.getDrawableIcon(file))
                                yield()
                                error(StorageHelper.getDrawableIcon(file))
                                transformations(CircleCropTransformation())
                                crossfade(true)
                            }
                        } catch (e: Exception) {
                            cancel()
                        } finally {
                            cancel()
                        }
                }

            }
            StorageHelper.Format.TXT -> imageView.load(StorageHelper.getDrawableIcon(file))
            StorageHelper.Format.VIDEO -> {
                CoroutineScope(Dispatchers.IO + job).launch {
                    try {
                        val bitmap = Previewer.getThumbnailFromLocalVideoUri(file.toUri())
                        imageView.load(bitmap) {
                            placeholder(StorageHelper.getDrawableIcon(file))
                            yield()
                            error(StorageHelper.getDrawableIcon(file))
                            transformations(CircleCropTransformation())
                            crossfade(true)
                        }
                    } catch (e: Exception) {
                        cancel()
                    } finally {
                        cancel()
                    }

                }

            }
            StorageHelper.Format.MUSIC -> imageView.load(StorageHelper.getDrawableIcon(file))
            StorageHelper.Format.IMAGE -> {
                imageView.load(file) {
                    placeholder(StorageHelper.getDrawableIcon(file))
                    error(StorageHelper.getDrawableIcon(file))
                    transformations(CircleCropTransformation())
                    crossfade(true)
                }
            }
            null -> imageView.load(R.drawable.ic_file)
        }

    } else imageView.load(R.drawable.ic_folder)
}

