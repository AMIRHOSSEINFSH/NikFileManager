package com.android.filemanager.features.highProcess

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import com.android.filemanager.R
import com.android.filemanager.core.BaseActivity
import com.android.filemanager.core.PROCESS_TYPE
import com.android.filemanager.core.Process
import com.android.filemanager.core.SOURCE_LIST_KYE
import com.android.filemanager.databinding.ActivityCopyCutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CopyCutActivity : BaseActivity<ActivityCopyCutBinding>(R.layout.activity_copy_cut) {

    private val viewModel: CopyCutViewModel by viewModels()


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val processType = intent.getParcelableExtra<Process>(SOURCE_LIST_KYE)
        val sourceList = intent.getStringArrayExtra(PROCESS_TYPE)
        if (processType == null || sourceList.isNullOrEmpty()) finish()
        else {
            viewModel.setProcessType(processType)
            viewModel.setSourcePathList(sourceList.toList())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}