package com.android.filemanager.core

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

fun StringBuffer.popLast():Boolean {
    val index = this.lastIndexOf(FILESEPRATOR)
   return try {
        this.delete(index,this.length)
       true
    }catch (e: IndexOutOfBoundsException){
        false
    }
}

