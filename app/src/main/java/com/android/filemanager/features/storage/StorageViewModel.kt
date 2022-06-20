package com.android.filemanager.features.storage

import android.os.Environment
import androidx.lifecycle.*
import com.android.filemanager.core.*
import com.android.filemanager.domain.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
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

    val isLinear: LiveData<Boolean> = stateHandle.getLiveData(IS_LINEAR, true)

    fun emitOnViewTypeChanged() {
        stateHandle[IS_LINEAR] = isLinear.value?.not() ?: false
    }


    private val _isSelected = MutableLiveData(0)
    val isSelected: LiveData<Int> get() = _isSelected

    private val _cancelSelection = MutableLiveData<Boolean>()
    val cancelSelection: LiveData<Boolean> get() = _cancelSelection

    fun emitOnClearSelectionList() {
        emitOnViewSelected(0)
    }

    fun emitOnViewSelected(number: Int) {
        if (number == 0) _cancelSelection.value = true
        _isSelected.value = number
    }

    private val _shouldClearSelection = MutableLiveData<Boolean>()
    val shouldClearSelection: LiveData<Boolean> get() = _shouldClearSelection

    fun emitOnAllViewSelected(shouldClear: Boolean) {
        _shouldClearSelection.value = if (shouldClear) {
            emitOnViewSelected(0)
            true
        } else {
            emitOnViewSelected(fileListLiveData.value?.size ?: 0)
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

    private val _parentPathLiveData= MutableLiveData<String?>()
    val parentPathLiveData: LiveData<String?> get() = _parentPathLiveData

    fun emitOnParentPath(path: String?) {
        _parentPathLiveData.value = path
    }

    private val _fileListLiveData = MutableLiveData<List<File>>()
    val fileListLiveData: LiveData<List<File>> get() = _fileListLiveData

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
            _fileListLiveData.value = listFileStack.pop()
            return
        }
        val rootPath = path ?: Environment.getExternalStorageDirectory().path
        size = getSize.invoke(rootPath)
        viewModelScope.launch(Dispatchers.IO) {
            fileInputIsLock = true
            val list = getFileList.invoke(rootPath, 10)
            withContext(Dispatchers.Main) {
                fileInputIsLock = false
                _fileListLiveData.value = list
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

    private val d =MediatorLiveData<Int>()
    fun createFolder(path: String, name: String): Boolean = fileIsCreated.invoke(path,name)

    fun deleteFileOrFolder(paths: List<File>) {
        viewModelScope.launch(Dispatchers.Default) {

        }
    }

}