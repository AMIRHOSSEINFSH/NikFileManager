package com.android.filemanager.domain

import com.android.filemanager.data.model.repository.StorageRepository
import javax.inject.Inject

class FreeInternal @Inject constructor(private val repo: StorageRepository) {

    operator fun invoke(): Long = repo.getAvailableInternalMemorySize()

}