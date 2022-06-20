package com.android.filemanager.data.model.repository

import androidx.lifecycle.LiveData
import com.android.filemanager.data.model.dataClass.FileModel
import java.io.File

interface StorageRepository {
    fun externalMemoryAvailable(): Boolean
    fun getAvailableInternalMemorySize(): Long
    fun getUsedInternalMemorySize(): Long
    fun getTotalInternalMemorySize(): Long
    fun getAvailableExternalMemorySize(): Long
    fun getTotalExternalMemorySize(): Long
    fun getRecentFilesLiveData(): LiveData<List<FileModel>>
    fun getListSize(path: String): Int
    fun createFolder(path: String,name: String):Boolean
    suspend fun deleteFileOrFolder(paths: List<File>): LiveData<Int>
   suspend fun getFileList(path: String,counter: Int): List<File>
}