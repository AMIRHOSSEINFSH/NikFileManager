package com.android.filemanager.data.model.dataClass

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FileModel(
    @PrimaryKey
    val path: String,
    var name: String,
    val isDirectory: Boolean = false,
    var size: Int = 0,
    var children: Int = 0
)