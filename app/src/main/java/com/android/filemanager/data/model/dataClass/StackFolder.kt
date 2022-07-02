package com.android.filemanager.data.model.dataClass

import java.io.File

data class StackFolder(
     val fullPath: String,
     val name: String,
     var files: List<File>
)
