package com.android.filemanager.core

import androidx.lifecycle.*
import com.android.filemanager.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import java.io.File
import java.util.*

class StorageHelper {
    fun createFolder(path: String, name: String): Boolean {
        val file = File("$path/$name")
        return if (!file.exists()) {
            file.mkdir()
        } else false
    }

    private val fileList = mutableListOf<File>()
    private val _indexCounter = MutableLiveData(0)

    suspend fun deleteFolderOrFile(
        files: List<File>,
        doOnFinished: suspend () -> Unit
    ) = liveData<Resource<Int>>(Dispatchers.IO) {
        try {
            files.forEach { file ->
                deleteRecursive(file)
            }
            if (fileList.isNotEmpty())
                emit(Resource.Loading(fileList.size))
            fileList.forEachIndexed { index, file ->
                yield()
                val result = file.delete()
                if (result) {
                    emit(Resource.Success(index + 1))
                } else {
                    emit(Resource.Error(DELETE_FAILED, file.hashCode()))
                }
            }
            files.forEach {
                deleteRecursiveFolders(it)
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: DELETE_FAILED))
        } finally {
            emit(Resource.Finished())
            doOnFinished.invoke()
        }

    }

    private fun deleteRecursiveFolders(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) for (child in fileOrDirectory.listFiles()!!) deleteRecursiveFolders(
            child
        )
        fileOrDirectory.delete()
    }

    private fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) for (child in fileOrDirectory.listFiles()!!) deleteRecursive(
            child
        )
        if (fileOrDirectory.isFile) {
            fileList.add(fileOrDirectory)
            //indexDeleted++
            // _indexCounter.value = indexDeleted
        }
        // fileOrDirectory.delete()
    }


    companion object {

        private fun getExtension(name: String): String {
            val lastIndexOf = name.lastIndexOf(".")
            return if (lastIndexOf == -1) {
                "" // empty extension
            } else {
                name.substring(lastIndexOf)
            }
        }

        fun getDrawableIcon(file: File): Int {
            val name = file.name

            return when (map[getExtension(name)]) {
                Format.ZIP -> R.drawable.ic_zip
                Format.PDF -> R.drawable.ic_pdf
                Format.TXT -> R.drawable.ic_txt
                Format.VIDEO -> R.drawable.ic_video
                Format.MUSIC -> R.drawable.ic_music
                Format.IMAGE -> R.drawable.ic_icons8_image_file
                null -> R.drawable.ic_file
            }
        }

        private val map = Hashtable<String, Format>().apply {
            put(".zip", Format.ZIP)
            put(".pdf", Format.PDF)
            put(".txt", Format.TXT)
            put(".mp4", Format.VIDEO)
            put(".mwv", Format.VIDEO)
            put(".mp3", Format.MUSIC)
            put(".jpg", Format.IMAGE)
            put(".jpeg", Format.IMAGE)
            put(".png", Format.IMAGE)
            put(".svg", Format.IMAGE)
        }

        fun compressName(name: String): String {
            val fromFirst = name.substring(0, 4)
            val extensionIndex = name.lastIndexOf(getExtension(name), ignoreCase = true)
            val fromLast = if (extensionIndex == 0) {
                name.substring(name.length - 2)
            } else {
                name.substring(extensionIndex - 2)
            }
            return "$fromFirst..$fromLast"
        }


    }

    private enum class Format {
        ZIP,
        PDF,
        TXT,
        VIDEO,
        MUSIC,
        IMAGE
    }


}