package com.android.filemanager.domain

import com.android.filemanager.data.model.repository.StorageRepository
import javax.inject.Inject

class CreateFile @Inject constructor(private val repo: StorageRepository) {

    operator fun invoke(path: String,name: String): Boolean = repo.createFolder(path,name)
}