package com.android.filemanager.core

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.widget.Toast
import com.android.filemanager.data.model.dataClass.StackFolder
import kotlinx.parcelize.Parcelize
import java.io.File

sealed class DataStorePreferences<T>(val value: T) {
    class THEME(mode: Int) : DataStorePreferences<Int>(mode)
}

fun formatSize(size: Long): String {
    var size = size
    var suffix: String? = null
    if (size >= 1024) {
        suffix = "KB"
        size /= 1024
        if (size >= 1024) {
            suffix = "MB"
            size /= 1024
        }
    }
    val resultBuffer = StringBuilder(size.toString())
    var commaOffset = resultBuffer.length - 3
    while (commaOffset > 0) {
        resultBuffer.insert(commaOffset, ',')
        commaOffset -= 3
    }
    if (suffix != null) resultBuffer.append(suffix)
    return resultBuffer.toString()
}

fun StringBuffer.addWithPrefix(name: String) {
    this.append(FILESEPRATOR + name)
}

fun StringBuffer.popLast(): Boolean {
    val index = this.lastIndexOf(FILESEPRATOR)
    return try {
        this.delete(index, this.length)
        true
    } catch (e: IndexOutOfBoundsException) {
        false
    }
}

 fun Context.queryCursor(
    uri: Uri,
    projection: Array<String>,
    selection: String? = null,
    selectionArgs: Array<String>? = null,
    sortOrder: String? = null,
    showErrors: Boolean = false,
    callback: (cursor: Cursor) -> Unit
) {
    try {
        val cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder)
        cursor?.use {
            if (cursor.moveToFirst()) {
                do {
                    callback(cursor)
                } while (cursor.moveToNext())
            }
        }
    } catch (e: Exception) {
        if (showErrors) {
            Toast.makeText(this, e.message ?: UNKNOWN_ERROR, Toast.LENGTH_SHORT).show()
        }
    }
}

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Finished<T>(message: String? = null, data: T? = null, result: Map<T, Boolean>? = null) :
        Resource<T>(data, message)

    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}

sealed class Process(
    val data: List<String>
) : Parcelable {
    @Parcelize
    data class Copy(val list: List<String>) : Process(list)

    @Parcelize
    data class Cut(val list: List<String>) : Process(list)
}

sealed class ActionOnList(val path: String? = null) {
    class ADD(val newPath: String) : ActionOnList(newPath)
    class POP(val stackFolder: StackFolder?) : ActionOnList(stackFolder?.fullPath)
    class REFRESH() : ActionOnList()
}


