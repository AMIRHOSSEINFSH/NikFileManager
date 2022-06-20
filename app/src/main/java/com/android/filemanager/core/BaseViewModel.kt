package com.android.filemanager.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel(): ViewModel() {

    protected val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    protected val refreshing = MutableLiveData(true)

    fun refresh(shouldRefresh: Boolean) {
        refreshing.value = shouldRefresh
    }

}