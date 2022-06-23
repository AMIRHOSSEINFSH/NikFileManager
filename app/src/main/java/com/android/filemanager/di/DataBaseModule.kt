package com.android.filemanager.di

import android.content.Context
import androidx.room.Room
import com.android.filemanager.core.DataStoreManager
import com.android.filemanager.data.model.dataBase.FileManagerDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, FileManagerDataBase::class.java, "FileManagerDB").build()

    /*@Provides
    @Singleton
    fun provideDataStore(@ApplicationContext appContext: Context)=
        DataStoreManager(appContext)*/

}