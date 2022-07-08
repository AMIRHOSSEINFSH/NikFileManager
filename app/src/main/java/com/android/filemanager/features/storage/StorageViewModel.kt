package com.android.filemanager.features.storage

import android.os.Environment
import android.util.Log
import androidx.lifecycle.*
import com.android.filemanager.core.*
import com.android.filemanager.data.model.dataClass.FileModel
import com.android.filemanager.data.model.dataClass.StackFolder
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
    private val insertRecent: InsertFileModels,
    private val dataStoreManager: DataStoreManager
) : BaseViewModel() {

    private fun emitOnProcessLock(isOnProcess: Boolean): Boolean {
        processIsWorking = isOnProcess
        return isOnProcess
    }

    var isFirst = true
    private val parentJob = Job()
    val isLinear: LiveData<Boolean> = stateHandle.getLiveData(IS_LINEAR, true)

    override fun emitOnViewTypeChanged() {
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

    fun insertRecentFiles(list: List<File>) {
        insertRecent.invoke(list.map { FileModel(it.path, it.name) })
    }

    val recentFilesList = refreshing.switchMap {
        recentFiles.invoke()
    }

    private val _parentPathLiveData = MutableLiveData<String?>()
    val parentPathLiveData: LiveData<String?> get() = _parentPathLiveData

    fun emitOnParentPath(path: String?) {
        _parentPathLiveData.value = path
    }

    fun getPercentageUsed(): Long {
        val used = getUsedInternal.invoke()
        val free = getFreeInternal.invoke()
        val result = used / (used + free).toFloat() * 100
        return result.toLong()
    }

    private fun String.getLastNameOfPath(): String {
        val array = this.split("/")
        return array[array.size - 1]
    }

    private var lastObjInStack: StackFolder? = null

    fun setRootOfLastStack(fullPath: String?) {

        viewModelScope.launch(parentJob + Dispatchers.Default) {

            val list = getFileList.invoke(fullPath ?: rootPath, 10)

            withContext(Dispatchers.Main) {
                lastObjInStack = StackFolder(
                    fullPath ?: rootPath,
                    fullPath?.getLastNameOfPath() ?: rootPath,
                    list
                )
                emitOnFileList(list, fullPath)
            }

        }

    }

    fun getLastObjInStack() = lastObjInStack

    //todo warning:it is necessary to   call these functions in backPressed callback function
    fun setLastObjInStack(fullPath: String?) {
        val stackFolder = StackFolder(
            fullPath ?: rootPath,
            fullPath?.getLastNameOfPath() ?: rootPath,
            _fileListLiveData.value.orEmpty()
        )
        lastObjInStack = stackFolder
    }


    fun doOnDataList(action: ActionOnList) {
        when (action) {
            is ActionOnList.ADD -> {
                viewModelScope.launch(parentJob + Dispatchers.Default) {

                    val list = getFileList.invoke(action.newPath, 10)
                    withContext(Dispatchers.Main) {
                        stack.add(lastObjInStack)
                        emitOnFileList(list, action.newPath)
                        setLastObjInStack(action.newPath)
                        stackLiveData.value = stack
                    }

                }
            }
            is ActionOnList.POP -> {
                //todo this use when we have Access to StackFolder Object (StackFolder object is accessible only from recyclerView adapter)
                if (action.stackFolder != null) {
                    var indexed: Int? = null
                    stack.forEachIndexed { index, stackFolder ->
                        if (stackFolder.fullPath == action.path) {
                            indexed = index
                            return
                        }
                    }
                    for (i in indexed!!..stack.size) {
                        stack.pop()
                    }
                    //val currentState = stack.peek()
                    emitOnFileList(action.stackFolder.files, action.path)
                    stackLiveData.value = stack
                }
                //todo this use when we are doing backward with one time pop
                else {
                    val lastStack = stack.pop()
                    lastObjInStack = lastStack
                    emitOnFileList(lastStack.files, lastStack.fullPath)
                    stackLiveData.value = stack
                }

            }
            is ActionOnList.REFRESH -> TODO()
        }
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
        size = getSize.invoke(rootPath)
        viewModelScope.launch(Dispatchers.IO) {
            fileInputIsLock = true
            val list = getFileList.invoke(rootPath, 10)
            withContext(Dispatchers.Main) {
                fileInputIsLock = false
                emitOnFileList(list, rootPath)
            }
        }
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
                    isSelected.value ?: emptyList(), doOnFinished = {
                        withContext(Dispatchers.Main) {
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