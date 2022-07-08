package com.android.filemanager.core

import android.os.Environment.*
import android.webkit.MimeTypeMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.android.filemanager.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.yield
import java.io.File
import java.util.*
import kotlin.collections.set


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

        // url = file path or whatever suitable URL you want.
        fun getMimeType(url: String?): String {
            var type: String? = null
            val extension = MimeTypeMap.getFileExtensionFromUrl(url)
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            }
            if (type == null) throw NullPointerException("type is inValid!")
            return type
        }

        fun getFormatType(file: File): Format? =
            map[getExtension(file.name.lowercase(Locale.getDefault()))]


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

    private val copyFilesLiveData = MutableLiveData<Resource<File>>()
    private val cutFilesLiveData = MutableLiveData<Resource<File>>()
    suspend fun copyFileToDestination(
        source: List<File>,
        destination: String
    ): LiveData<Resource<File>> =
        liveData {
            this.emitSource(copyFilesLiveData)
            val result = HashMap<File, Boolean>()
            source.forEach { rootFile ->
                result[rootFile] =
                    rootFile.copyRecursively(
                        File("$destination/${rootFile.name}"),
                        onError = { fileError, ioException ->
                            copyFilesLiveData.value = Resource.Error(
                                ioException.message ?: UNKNOWN_ERROR, fileError
                            )
                            OnErrorAction.SKIP
                        })
            }
            copyFilesLiveData.value = Resource.Finished(result = result)
        }

    suspend fun cutFilesToDestination(
        sources: List<File>,
        destination: String
    ): LiveData<Resource<File>> = liveData {
        emitSource(cutFilesLiveData)
        sources.forEach { rootFile ->
            val copyStatus =
                rootFile.copyRecursively(
                    File("${destination}/${rootFile.name}"),
                    onError = { fileError, ioException ->
                        cutFilesLiveData.value = Resource.Error(
                            ioException.message ?: UNKNOWN_ERROR, fileError
                        )

                        OnErrorAction.SKIP
                    })
            val removeStatus = rootFile.deleteRecursively()
            if (copyStatus && removeStatus) cutFilesLiveData.value = Resource.Success(rootFile)
            else cutFilesLiveData.value = Resource.Error(CUT_FACED_ERROR, rootFile)
        }
        cutFilesLiveData.value = Resource.Finished()
    }

    enum class Format {
        ZIP,
        PDF,
        TXT,
        VIDEO,
        MUSIC,
        IMAGE
    }

    enum class MimTypes(val value: String) {
        IMAGE("images"),
        VIDEO("videos"),
        Audio("audio"),
        DOCUMENTS("documents"),
        ARCHIVES("archives"),
        OTHERS("others")
    }


}