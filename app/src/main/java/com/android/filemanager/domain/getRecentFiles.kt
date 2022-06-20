package com.android.filemanager.domain

import androidx.lifecycle.LiveData
import com.android.filemanager.data.model.dataClass.FileModel
import com.android.filemanager.data.model.repository.StorageRepository
import javax.inject.Inject

class getRecentFiles @Inject constructor(private val repo: StorageRepository) {

    operator fun invoke(): LiveData<List<FileModel>> = repo.getRecentFilesLiveData()

}