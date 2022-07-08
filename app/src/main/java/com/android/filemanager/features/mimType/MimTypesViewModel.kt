package com.android.filemanager.features.mimType

import android.provider.MediaStore
import androidx.lifecycle.viewModelScope
import com.android.filemanager.core.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MimTypesViewModel @Inject constructor(

) : BaseViewModel() {
    override fun emitOnViewTypeChanged() {

    }

    override fun getPath(path: String?) {

    }

    fun refetchItems() {
        /*viewModelScope.launch {
            getProperFileDirItems { fileDirItems ->
                val listItems = getListItemsFromFileDirItems(fileDirItems)

                withContext(Dispatchers.Main) {
                    addItems(listItems)
                    if (currentViewType != config.getFolderViewType(currentMimeType)) {
                        setupLayoutManager()
                    }
                }
            }

        }*/


    }





}