package com.android.filemanager.features

import android.app.Application
import com.cops.iitbhu.previewer.lib.Previewer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NikApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Previewer.init(this)
    }
}