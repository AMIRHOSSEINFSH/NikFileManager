package com.android.filemanager.di

import com.android.filemanager.core.StorageHelper
import com.android.filemanager.data.model.dao.FileDao
import com.android.filemanager.data.model.repository.StorageRepository
import com.android.filemanager.data.model.repository.StorageRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideStorageHelper() = StorageHelper()

    @Provides
    @Singleton
    fun provideStorageRepo(dao: FileDao,storageHelper: StorageHelper) = StorageRepositoryImpl(dao,storageHelper) as StorageRepository

}