package com.android.filemanager.data.model.repository

import android.os.Environment
import android.os.StatFs
import androidx.lifecycle.LiveData
import com.android.filemanager.core.Resource
import com.android.filemanager.core.StorageHelper
import com.android.filemanager.data.model.dao.FileDao
import com.android.filemanager.data.model.dataClass.FileModel
import java.io.File
import javax.inject.Inject

class StorageRepositoryImpl @Inject constructor(
    private val dao: FileDao,
    private val storageHelper: StorageHelper
) : StorageRepository {

    override fun externalMemoryAvailable(): Boolean =
        Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)

    override fun getAvailableInternalMemorySize(): Long {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSizeLong
        val availableBlocks = stat.availableBlocksLong
        return availableBlocks * blockSize
    }

    override fun getUsedInternalMemorySize(): Long {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        val availableBlocks = stat.availableBlocksLong
        return (totalBlocks - availableBlocks) * blockSize
    }

    override fun getTotalInternalMemorySize(): Long {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        return totalBlocks * blockSize
    }

    override fun getAvailableExternalMemorySize(): Long {
        return if (externalMemoryAvailable()) {
            val path = Environment.getExternalStorageDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSizeLong
            val availableBlocks = stat.availableBlocksLong
            availableBlocks * blockSize
        } else {
            0
        }
    }

    override fun getTotalExternalMemorySize(): Long {
        return if (externalMemoryAvailable()) {
            val path = Environment.getExternalStorageDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSizeLong
            val totalBlocks = stat.blockCountLong
            totalBlocks * blockSize
        } else {
            0
        }
    }

    override fun getRecentFilesLiveData(): LiveData<List<FileModel>> {
        return dao.getRecentFiles()
    }

    override fun getListSize(path: String): Int {
        return File(path).listFiles()?.size ?: 0
    }

    override fun createFolder(path: String, name: String): Boolean =
        storageHelper.createFolder(path, name)

    override suspend fun deleteFileOrFolder(
        paths: List<File>,
        doOnFinised: suspend () -> Unit
    ): LiveData<Resource<Int>> = storageHelper.deleteFolderOrFile(paths,doOnFinised)


    override suspend fun getFileList(path: String, counter: Int): List<File> {
        return run {
            val list = File(path).listFiles()?.toList().orEmpty()
            list
        }
    }

}