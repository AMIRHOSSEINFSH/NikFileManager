package com.android.filemanager.features.category

import androidx.lifecycle.*
import com.android.filemanager.core.BaseViewModel
import com.android.filemanager.core.IS_LINEAR
import com.android.filemanager.core.StorageHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val stateHandle: SavedStateHandle,
    private val storageHelper: StorageHelper
) : BaseViewModel() {

    val isLinear: LiveData<Boolean> = stateHandle.getLiveData(IS_LINEAR, true)

   override fun emitOnViewTypeChanged() {
        stateHandle[IS_LINEAR] = isLinear.value?.not() ?: false
    }

    private var category: String? = null
    private val refreshList = MutableLiveData(true)
    private fun emitOnRefresh() {
        refreshList.value = true
    }
    val listLiveData: LiveData<List<File>> = refreshList.switchMap {
        liveData {
            try {
                val category = StorageHelper.Category.valueOf(category?:"")
                emitSource(storageHelper.getCategoryList(category))
            }catch (e: Exception) {
                _errorLiveData.value = e.message
            }
        }
    }
    fun setCategory(category: String) {
        this.category = category
    }


    override fun getPath(path: String?) {
        emitOnRefresh()
    }


}