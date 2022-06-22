package com.android.filemanager.features.highProcess

import com.android.filemanager.core.BaseViewModel
import com.android.filemanager.core.Process
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CopyCutViewModel @Inject constructor() : BaseViewModel() {


    private lateinit var processType: Process
    private lateinit var sourcePathList: List<String>

    fun setProcessType(type: Process) {
        processType = type
    }

    fun setSourcePathList(list: List<String>) {
        sourcePathList = list
    }

    fun getProcessType() = processType
    fun getSourcePathList() = sourcePathList







}