package com.android.filemanager.features.storage

import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.ColorFilter
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.filemanager.R
import com.android.filemanager.core.BaseFragment
import com.android.filemanager.databinding.FragmentStorageBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class StorageFragment : BaseFragment<FragmentStorageBinding>(R.layout.fragment_storage) {

    private val viewModel: StorageViewModel by activityViewModels()

    private val adapter by lazy {
        RecentFilesAdapter { fileModel ->

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