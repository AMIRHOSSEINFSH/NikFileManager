package com.android.filemanager.core

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.filemanager.features.files.FilesFragment
import com.android.filemanager.features.storage.StorageFragment

class DemoCollectionAdapter(manager: FragmentManager,lifeCycle: Lifecycle) : FragmentStateAdapter(manager,lifeCycle) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
       return if (position == 0) {
           StorageFragment()
       }else {
            FilesFragment()
       }
    }
}
