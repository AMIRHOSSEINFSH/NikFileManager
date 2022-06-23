package com.android.filemanager.domain

import com.android.filemanager.data.model.repository.StorageRepository
import java.io.File
import javax.inject.Inject

class CutFilesToDestination @Inject constructor(private val repo: StorageRepository) {

    suspend operator fun invoke(sources: List<String>,destination: String) = repo.doCutFiles(sources,destination)
}