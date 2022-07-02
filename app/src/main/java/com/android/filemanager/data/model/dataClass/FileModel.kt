package com.android.filemanager.data.model.dataClass

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FileModel(
    @PrimaryKey
    val path: String,
    var name: String? = null
)