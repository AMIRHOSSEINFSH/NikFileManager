package com.android.filemanager.domain

import androidx.lifecycle.LiveData
import com.android.filemanager.core.Resource
import com.android.filemanager.data.model.repository.StorageRepository
import java.io.File
import javax.inject.Inject

class DoCopyFiles @Inject constructor(private val repo: StorageRepository) {

    suspend operator fun invoke(sources: List<String>, destination: String): LiveData<Resource<File>> =
        repo.copyFilesToDestination(sources, destination)

}