package com.android.filemanager.core

import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File
import java.util.*

abstract class BaseViewModel() : ViewModel() {

    protected val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    protected val refreshing = MutableLiveData(true)

    fun refresh(shouldRefresh: Boolean) {
        refreshing.value = shouldRefresh
    }

    var processIsWorking: Boolean = false

    protected val listFileStack = Stack<List<File>>()
    protected var order = 0
    protected val rootPath = Environment.getExternalStorageDirectory().path
    protected val strBuffer = StringBuffer()
    protected var currentPath: String = rootPath
    var fileInputIsLock = false
    //todo true means pair is add mode(forward navigate) and false means pair is pop mode(backWard Navigate)
    protected val _pathDirectoryLiveData = MutableLiveData<Pair<Boolean,String>>()
    val pathDirectoryLiveData: LiveData<Pair<Boolean,String>> get() = _pathDirectoryLiveData
    protected val _fileListLiveData = MutableLiveData<List<File>>()
    val fileListLiveData: LiveData<List<File>> get() = _fileListLiveData
    protected fun emitOnFileList(list: List<File>, newCurrentPath: String?) {
        currentPath = newCurrentPath ?: rootPath
        _fileListLiveData.value = list
    }

    init {
        addNewPath(rootPath.removePrefix("/"))
    }

    fun getCurrent() = currentPath
    fun addNewPath(name: String) {
        order++
        strBuffer.addWithPrefix(name)
        _pathDirectoryLiveData.value = Pair(true,/*strBuffer.toString()*/"/$name")
        listFileStack.add(fileListLiveData.value)
    }

    fun popLastPath() {
        order--
        strBuffer.popLast()
        _pathDirectoryLiveData.value = Pair(false,strBuffer.toString())
    }

}