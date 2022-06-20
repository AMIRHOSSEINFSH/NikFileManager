package com.android.filemanager.di

import com.android.filemanager.data.model.dao.FileDao
import com.android.filemanager.data.model.dataBase.FileManagerDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Provides
    @Singleton
    fun provideFileDao(db: FileManagerDataBase): FileDao = db.getFileDao()

}