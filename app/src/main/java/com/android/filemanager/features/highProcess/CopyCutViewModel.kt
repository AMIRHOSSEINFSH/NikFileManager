package com.android.filemanager.features.highProcess

import androidx.lifecycle.*
import com.android.filemanager.core.BaseViewModel
import com.android.filemanager.core.IS_LINEAR
import com.android.filemanager.core.Process
import com.android.filemanager.core.Resource
import com.android.filemanager.data.model.dataClass.FileModel
import com.android.filemanager.domain.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CopyCutViewModel @Inject constructor(
    private val stateHandle: SavedStateHandle,
    private val copyFiles: DoCopyFiles,
    private val cutFiles: CutFilesToDestination,
    private val fileIsCreated: CreateFile,
    private val getFileList: getFileList,
    private val insertFileModels: InsertFileModels
) : BaseViewModel() {


    private lateinit var processType: Process
    private lateinit var sourcePathList: List<String>
    var destinationPath = rootPath

    val isLinear: LiveData<Boolean> = stateHandle.getLiveData(IS_LINEAR, true)

    override fun emitOnViewTypeChanged() {
        stateHandle[IS_LINEAR] = isLinear.value?.not() ?: false
    }

    override fun getPath(
        path: String?
    ) {
        if (listFileStack.getOrNull(order) != null) {
            val list = listFileStack.pop()
            emitOnFileList(list, list.first().parent)
            return
        }
        val rootPath = path ?: this.rootPath
        viewModelScope.launch(Dispatchers.IO) {
            fileInputIsLock = true
            val list = getFileList.invoke(rootPath, 10)
            withContext(Dispatchers.Main) {
                fileInputIsLock = false
                emitOnFileList(list, rootPath)
            }
        }
    }

    fun setProcessType(type: Process) {
        processType = type
    }

    fun setSourcePathList(list: List<String>) {
        sourcePathList = list
    }

    fun getProcessType() = processType
    fun getSourcePathList() = sourcePathList

    fun createFolder(path: String, name: String): Boolean = fileIsCreated.invoke(path, name)

    private val shouldCopy = MutableLiveData<Boolean>()
    private val shouldCut = MutableLiveData<Boolean>()

    val copyLiveData = shouldCopy.switchMap {
        liveData(Dispatchers.Default) {
            emitSource(copyFiles.invoke(sourcePathList, destinationPath))
            insertFileModels.invoke(sourcePathList.map { FileModel(it) })
        }
    }

    val cutLiveData = shouldCut.switchMap {
        liveData(Dispatchers.Default) {
            emitSource(cutFiles.invoke(sourcePathList, destinationPath))
        }
    }

    private fun emitOnCopy(permissionGranted: () -> Boolean) {
        if (permissionGranted.invoke()) {
            shouldCopy.value = true
        }
    }

    private fun emitOnCut(permissionGranted: () -> Boolean) {
        if (permissionGranted.invoke()) {
            shouldCut.value = true
        }
    }

    fun emitOnTransferredFiles(permissionGranted: () -> Boolean) {
        when (processType) {
            is Process.Copy -> emitOnCopy { permissionGranted.invoke() }
            is Process.Cut -> emitOnCut { permissionGranted.invoke() }
        }
    }


}