package com.android.filemanager.domain

import com.android.filemanager.data.model.dataClass.FileModel
import com.android.filemanager.data.model.repository.StorageRepository
import javax.inject.Inject

class InsertFileModels @Inject constructor(private val repository: StorageRepository) {

    operator fun invoke(list: List<FileModel>) = repository.insertFileModifies(list)
}