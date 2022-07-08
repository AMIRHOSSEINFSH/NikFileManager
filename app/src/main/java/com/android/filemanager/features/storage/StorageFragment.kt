package com.android.filemanager.features.storage

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.filemanager.R
import com.android.filemanager.core.*
import com.android.filemanager.databinding.FragmentStorageBinding
import com.android.filemanager.features.mimType.MimTypesActivity
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class StorageFragment : BaseFragment<FragmentStorageBinding>(R.layout.fragment_storage) {

    private val viewModel: StorageViewModel by activityViewModels()

    private val adapter by lazy {
        RecentFilesAdapter { fileModel ->
            try {
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                intent.type = StorageHelper.getMimeType(fileModel.path)
                intent.data = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().applicationContext.packageName + ".provider",
                    fileModel
                )
                startActivity(intent)
            } catch (e: Exception) {
                showMessage(e.message ?: UNKNOWN_ERROR)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initSetUp()
        setUpAdapters()
        setUpObservables()
        setUpListeners()
    }

    private fun setUpListeners() {
        binding.image.setOnClickListener {
            launchCategory(StorageHelper.MimTypes.IMAGE)
        }
        binding.video.setOnClickListener {
            launchCategory(StorageHelper.MimTypes.VIDEO)
        }
        binding.document.setOnClickListener {
            launchCategory(StorageHelper.MimTypes.DOCUMENTS)
        }
        binding.audio.setOnClickListener {
            launchCategory(StorageHelper.MimTypes.Audio)
        }
    }

    private fun launchCategory(type: StorageHelper.MimTypes) {
        Intent(context, MimTypesActivity::class.java).apply {
            putExtra(SHOW_MIMETYPE, type)
            startActivity(this)
        }
    }


    private fun initSetUp() {
        val list: List<String> = if (viewModel.isExternalMemoryAvailable()) {
            listOf(getString(R.string.internal), getString(R.string.external))
        } else {
            listOf(getString(R.string.internal))
        }

        binding.apply {
            spinner.setItems(list)
            usedSpace.text =
                viewModel.getUsedInternalMemory()
            freeSpace.text = viewModel.getFreeInternalMemory()
            startProgressBarAnimation(
                progressCircular,
                txtProgress,
                viewModel.getPercentageUsed().toInt()
            )
        }
    }

    private fun setUpObservables() {
        viewModel.recentFilesList.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list.map { File(it.path) })
        }
    }

    private fun setUpAdapters() {
        binding.recentFileMain.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.recentFileMain.adapter = adapter
    }

    private fun startProgressBarAnimation(
        progressBar: ProgressBar,
        txtProgress: TextView,
        progress: Int
    ) {
        progressBar.max = 100
        progressBar.progress = 0

        val progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", 0, progress)
        progressAnimator.duration = 1000
        progressAnimator.start()
        progressAnimator.addUpdateListener { animationUpdate ->
            val inProgressValue = animationUpdate?.animatedValue as Int
            txtProgress.text = "$inProgressValue%"
        }

    }

}