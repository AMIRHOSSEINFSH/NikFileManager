package com.android.filemanager.di

import androidx.lifecycle.SavedStateHandle
import com.android.filemanager.domain.*
import com.android.filemanager.features.storage.StorageViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ViewModelModule {


    /*@Provides
    @Singleton
    fun provideStorageViewModel(
        stateHandle: SavedStateHandle,
        isAvailable: IsSdAvailable,
        getUsedInternal: UsedInternal,
        getFreeInternal: FreeInternal,
        recentFiles: getRecentFiles,
        getFileList: getFileList,
        getSize: getListSize
    ): StorageViewModel {
        return StorageViewModel(
            stateHandle,
            isAvailable,
            getUsedInternal,
            getFreeInternal,
            recentFiles,
            getFileList,
            getSize
        )
    }*/
}