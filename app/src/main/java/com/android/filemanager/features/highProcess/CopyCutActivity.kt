package com.android.filemanager.features.highProcess

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatTextView
import com.android.filemanager.R
import com.android.filemanager.core.BaseActivity
import com.android.filemanager.core.PROCESS_TYPE
import com.android.filemanager.core.Process
import com.android.filemanager.databinding.ActivityCopyCutBinding
import com.android.filemanager.features.files.CreateFileFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CopyCutActivity : BaseActivity<ActivityCopyCutBinding>(R.layout.activity_copy_cut) {

    private val viewModel: CopyCutViewModel by viewModels()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()


    }

    private fun authenticateInputDatas(
        onSuccess: (processType: Process, List<String>) -> Unit,
        onFail: () -> Unit
    ) {
        val processType = intent.extras?.getParcelable<Process>(PROCESS_TYPE)
        if (processType == null || processType.data.isEmpty()) onFail()
        else {
            onSuccess(processType, processType.data)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("TAG", "onCreate: viewMo $viewModel")
        setUpListeners()
        setUpObservables()

        authenticateInputDatas(onSuccess = { processType, list ->
            viewModel.setProcessType(processType)
            viewModel.setSourcePathList(list)
        }, onFail = { finish() })
        binding.source.text = viewModel.getSourcePathList().first()
        viewModel.pathDirectoryLiveData.observe(this) {
            if (it.first)
                binding.destinationContainer.addView(
                    AppCompatTextView(this).apply { text = it.second },
                    ViewGroup.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                )
            else binding.destinationContainer.removeViewAt(binding.destinationContainer.childCount - 1)
        }
    }

    private fun setUpObservables() {

    }

    private fun setUpListeners() {
        binding.createNew.setOnClickListener {
            CreateFileFragment.newInstance(viewModel.getCurrent()){
                val str= viewModel.getCurrent()
                viewModel.getPath(str)
            }.show(supportFragmentManager, null)
        }
        binding.pasteBtn.setOnClickListener {
            viewModel.emitOnTransferredFiles { true }
        }

    }
}