package com.android.filemanager.features.storage

import android.os.Environment
import androidx.lifecycle.*
import com.android.filemanager.core.*
import com.android.filemanager.domain.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlin.io.path.Path


@HiltViewModel
class StorageViewModel @Inject constructor(
    private val stateHandle: SavedStateHandle,
    private val isAvailable: IsSdAvailable,
    private val getUsedInternal: UsedInternal,
    private val getFreeInternal: FreeInternal,
    private val recentFiles: getRecentFiles,
    private val getFileList: getFileList,
    private val getSize: getListSize,
    private val fileIsCreated: CreateFile,
    private val fileIsDeleted: DeleteFile,
    private val dataStoreManager: DataStoreManager
) : BaseViewModel() {

    var processIsWorking: Boolean = false

    fun emitOnProcessLock(isOnProcess: Boolean): Boolean {
        processIsWorking = isOnProcess
        return isOnProcess
    }
    val isLinear: LiveData<Boolean> = stateHandle.getLiveData(IS_LINEAR, true)
    private val parentJob = SupervisorJob()
    private val rootPath = Environment.getExternalStorageDirectory().path
    private var currentPath: String = rootPath

    fun emitOnViewTypeChanged() {
        stateHandle[IS_LINEAR] = isLinear.value?.not() ?: false
    }


    private val _isSelected = MutableLiveData<List<File>>(emptyList())
    val isSelected: LiveData<List<File>> get() = _isSelected

    private val _cancelSelection = MutableLiveData<Boolean>()
    val cancelSelection: LiveData<Boolean> get() = _cancelSelection

    fun emitOnClearSelectionList() {
        emitOnViewSelected(emptyList())
    }

    fun emitOnViewSelected(files: List<File>) {
        if (files.isEmpty()) _cancelSelection.value = true
        _isSelected.value = files
    }

    private val _shouldClearSelection = MutableLiveData<Boolean>()
    val shouldClearSelection: LiveData<Boolean> get() = _shouldClearSelection

    fun emitOnAllViewSelected(shouldClear: Boolean) {
        _shouldClearSelection.value = if (shouldClear) {
            emitOnViewSelected(emptyList())
            true
        } else {
            emitOnViewSelected(fileListLiveData.value ?: emptyList())
            false
        }
    }

    private var size = 0

    private val listFileStack = Stack<List<File>>()
    var fileInputIsLock = false
    private val strBuffer = StringBuffer()
    private var order = 0

    val recentFilesList = refreshing.switchMap {
        recentFiles.invoke()
    }

    private val _pathDirectoryLiveData = MutableLiveData<String>()
    val pathDirectoryLiveData: LiveData<String> get() = _pathDirectoryLiveData

    private val _parentPathLiveData = MutableLiveData<String?>()
    val parentPathLiveData: LiveData<String?> get() = _parentPathLiveData

    fun emitOnParentPath(path: String?) {
        _parentPathLiveData.value = path
    }

    private val _fileListLiveData = MutableLiveData<List<File>>()
    val fileListLiveData: LiveData<List<File>> get() = _fileListLiveData

    private fun emitOnFileList(list: List<File>,newCurrentPath: String?) {
        currentPath = newCurrentPath ?: rootPath
        _fileListLiveData.value = list
    }

    fun getPercentageUsed(): Long {
        val used = getUsedInternal.invoke()
        val free = getFreeInternal.invoke()
        val result = used / (used + free).toFloat() * 100
        return result.toLong()
    }

    fun getPath(
        path: String?
    ) {
        if (listFileStack.getOrNull(order) != null) {
            val list = listFileStack.pop()
            emitOnFileList(list,list.first().parent)
            return
        }
        val rootPath = path ?: this.rootPath
        size = getSize.invoke(rootPath)
        viewModelScope.launch(Dispatchers.IO) {
            fileInputIsLock = true
            val list = getFileList.invoke(rootPath, 10)
            withContext(Dispatchers.Main) {
                fileInputIsLock = false
                emitOnFileList(list,rootPath)
            }
        }
    }

    fun addNewPath(name: String) {
        order++
        strBuffer.addWithPrefix(name)
        _pathDirectoryLiveData.value = strBuffer.toString()
        listFileStack.add(fileListLiveData.value)

    }

    fun popLastPath() {
        order--
        strBuffer.popLast()
        _pathDirectoryLiveData.value = strBuffer.toString()
    }

    fun popToPath(name: String) {

    }

    fun isExternalMemoryAvailable(): Boolean = isAvailable.invoke()
    fun getUsedInternalMemory() = formatSize(getUsedInternal.invoke())
    fun getFreeInternalMemory() = formatSize(getFreeInternal.invoke())

    val permissionLiveData = dataStoreManager.getPermissionGranted().asLiveData()
    fun permissionGranted() {
        viewModelScope.launch {
            dataStoreManager.doPermissionGranted(true)
        }
    }

    fun createFolder(path: String, name: String): Boolean = fileIsCreated.invoke(path, name)

    private val shouldDelete = MutableLiveData<Boolean>()
    val delete: LiveData<Resource<Int>> = shouldDelete.switchMap {
        liveData {
            emitOnProcessLock(true)
            emitSource(
                fileIsDeleted.invoke(
                    isSelected.value ?: emptyList()
                , doOnFinished = {
                        withContext(Dispatchers.Main){
                            emitOnProcessLock(false)
                            emitOnAllViewSelected(true)
                            getPath(currentPath)
                        }
                    })
            )
        }
    }

    fun emitOnDelete() {
        shouldDelete.value = true
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

}