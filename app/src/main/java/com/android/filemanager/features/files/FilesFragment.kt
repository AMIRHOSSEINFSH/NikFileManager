package com.android.filemanager.features.files

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.filemanager.R
import com.android.filemanager.core.BaseFragment
import com.android.filemanager.databinding.FragmentFilesBinding
import com.android.filemanager.features.storage.RecentFilesAdapter
import com.android.filemanager.features.storage.StorageViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FilesFragment : BaseFragment<FragmentFilesBinding>(R.layout.fragment_files) {

    private val viewModel: StorageViewModel by activityViewModels()

    private val recentAdapter by lazy {
        RecentFilesAdapter { fileModel ->

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSetUp()
        setUpAdapters()
        setupListeners()

        binding.ivFilesChangeRotate.setOnClickListener {
            viewModel.emitOnViewTypeChanged()
        }

        viewModel.isLinear.observe(viewLifecycleOwner) {
            if (it) {
                binding.ivFilesChangeRotate.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_linear_24dp
                    )
                )
            } else {
                binding.ivFilesChangeRotate.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_grid_24dp
                    )
                )
            }

        }

        viewModel.pathDirectoryLiveData.observe(viewLifecycleOwner) { path ->
            binding.directory.text = path
        }


    }

    private fun setupListeners() {
        binding.createNew.setOnClickListener {
            CreateFileFragment.newInstance(viewModel.parentPathLiveData.value)
                .show(parentFragmentManager, null)
        }
    }

    private fun setUpAdapters() {
        binding.apply {

        }
    }

    private fun initSetUp() {
    }


}