package com.android.filemanager.features

import android.app.Application
import com.cops.iitbhu.previewer.lib.Previewer
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class NikApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Previewer.init(this)
        Timber.plant(Timber.DebugTree())
    }
}