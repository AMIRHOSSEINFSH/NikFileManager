package com.android.filemanager.domain

import com.android.filemanager.data.model.repository.StorageRepository
import java.io.File
import javax.inject.Inject

class getFileList @Inject constructor(private val repo: StorageRepository) {

    suspend operator fun invoke(path: String,counter: Int): List<File> = repo.getFileList(path,counter)

}