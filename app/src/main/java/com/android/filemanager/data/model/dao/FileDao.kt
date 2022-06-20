package com.android.filemanager.data.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android.filemanager.data.model.dataClass.FileModel

@Dao
interface FileDao {

    @Query("SELECT * From filemodel")
    fun getRecentFiles(): LiveData<List<FileModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFile(file: FileModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFiles(list: List<FileModel>): List<Long>

}