package com.android.filemanager.features.explore

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.filemanager.R
import com.android.filemanager.core.BaseFragment
import com.android.filemanager.databinding.FragmentFileExploringBinding
import com.android.filemanager.features.files.FileListAdapter
import com.android.filemanager.features.storage.StorageViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FileExploringFragment :
    BaseFragment<FragmentFileExploringBinding>(R.layout.fragment_file_exploring) {

    private val viewModel: StorageViewModel by activityViewModels()
    private val args: FileExploringFragmentArgs by navArgs()

    private var currentPath: String? = null

    private val fileExploreAdapter by lazy {
        FileListAdapter(itemClicked = { fileModel ->
            val shouldNavigate = viewModel.isSelected.value == 0
            if (shouldNavigate) {
                viewModel.addNewPath(fileModel.name)
                findNavController().navigate(
                    FileExploringFragmentDirections.actionFileExploringFragmentSelf()
                        .setPath(fileModel.path)
                )
            }
            shouldNavigate
        }, itemLongClicked = {
            viewModel.emitOnViewSelected(it.size)
        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i("LOGOG", "onViewCreated: ${viewModel.toString()}")
        initSetUp()
        setUpPermission()
        setUpAdapters()
        setUpObservables()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (findNavController().backQueue.size != 2) {
                        viewModel.popLastPath()
                        findNavController().popBackStack()
                    }
                }

            }
        )

    }

    private fun initSetUp() {
        currentPath = args.path
    }

    private fun setUpPermission() {
        viewModel.getPath(currentPath)
    }

    private fun setUpAdapters() {
        binding.apply {
            fileExploreRec.layoutManager =
                GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false)
            fileExploreRec.adapter = fileExploreAdapter
        }
    }

    private fun setUpObservables() {
        viewModel.fileListLiveData.observe(viewLifecycleOwner) { list ->
            viewModel.emitOnParentPath(list.first().parent)
            if (!viewModel.fileInputIsLock)
                fileExploreAdapter.submitList(list)
        }
        viewModel.isLinear.observe(viewLifecycleOwner) {
            if (it) {
                binding.fileExploreRec.layoutManager =
                    LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                fileExploreAdapter.setItemViewType(R.layout.item_file_linear)
            } else {
                binding.fileExploreRec.layoutManager =
                    GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false)
                fileExploreAdapter.setItemViewType(R.layout.item_file_grid)
            }
        }
        viewModel.cancelSelection.observe(viewLifecycleOwner) {
            fileExploreAdapter.clearSelectedList()
        }
        viewModel.shouldClearSelection.observe(viewLifecycleOwner){shouldCLear->
            if (shouldCLear) fileExploreAdapter.clearSelection() else fileExploreAdapter.selectAll()
        }
    }

}