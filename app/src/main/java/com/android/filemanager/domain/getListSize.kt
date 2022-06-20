package com.android.filemanager.domain

import com.android.filemanager.data.model.repository.StorageRepository
import javax.inject.Inject

class getListSize @Inject constructor(private val repo: StorageRepository) {

    operator fun invoke(path: String): Int = repo.getListSize(path)

}