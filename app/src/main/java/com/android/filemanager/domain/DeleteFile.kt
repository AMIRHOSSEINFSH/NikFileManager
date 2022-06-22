package com.android.filemanager.domain

import androidx.lifecycle.LiveData
import com.android.filemanager.core.Resource
import com.android.filemanager.data.model.repository.StorageRepository
import java.io.File
import javax.inject.Inject

class DeleteFile @Inject constructor(private val repo: StorageRepository) {

    suspend operator fun invoke(paths: List<File>, doOnFinished: suspend () -> Unit): LiveData<Resource<Int>> =
        repo.deleteFileOrFolder(paths,doOnFinished)
}