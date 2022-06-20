package com.android.filemanager.domain

import com.android.filemanager.data.model.repository.StorageRepository
import javax.inject.Inject

class IsSdAvailable @Inject constructor(private val repo: StorageRepository) {

    operator fun invoke(): Boolean = repo.externalMemoryAvailable()

}