package com.android.filemanager.data.model.dataBase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android.filemanager.data.model.dao.FileDao
import com.android.filemanager.data.model.dataClass.FileModel

@Database(entities = [FileModel::class] , version = 1, exportSchema = false)
abstract class FileManagerDataBase: RoomDatabase() {

    abstract fun getFileDao(): FileDao

}