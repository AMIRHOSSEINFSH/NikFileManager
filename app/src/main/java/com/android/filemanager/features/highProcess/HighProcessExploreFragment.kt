package com.android.filemanager.features.highProcess

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.filemanager.R
import com.android.filemanager.core.BaseFragment
import com.android.filemanager.core.Resource
import com.android.filemanager.databinding.FragmentHightProcessExploreBinding
import com.android.filemanager.features.explore.FileExploringFragmentArgs
import com.android.filemanager.features.explore.FileExploringFragmentDirections
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HighProcessExploreFragment :
    BaseFragment<FragmentHightProcessExploreBinding>(R.layout.fragment_hight_process_explore) {

    private val args: FileExploringFragmentArgs by navArgs()
    private var currentPath: String? = null
    private val viewModel by activityViewModels<CopyCutViewModel>()

    private val adapter by lazy {
        FileListNavigateAdapter { fileModel ->
            viewModel.destinationPath = fileModel.path
            viewModel.addNewPath(fileModel.name)
            findNavController().navigate(
                HighProcessExploreFragmentDirections.actionHighProcessExploreFragmentSelf2()
                    .setPath(fileModel.path)
            )

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSetUp()
        setUpOnBackPressed()
        setUpAdapter()
        setUpObservables()
    }

    private fun setUpOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!viewModel.processIsWorking) {
                        if (findNavController().backQueue.size != 2) {
                            viewModel.popLastPath()
                            findNavController().popBackStack()
                        }else {
                            requireActivity().setResult(RESULT_CANCELED)
                            requireActivity().finish()
                        }
                    }

                }

            }
        )
    }

    private fun setUpAdapter() {
        binding.apply {
            processRec.layoutManager =
                GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false)
            processRec.adapter = adapter
        }
    }

    private fun initSetUp() {
        currentPath = args.path
        viewModel.getPath(currentPath)
    }

    private fun setUpObservables() {
        viewModel.fileListLiveData.observe(viewLifecycleOwner) { list ->
            if (!viewModel.fileInputIsLock) {
                adapter.submitList(list)
            }
        }
        viewModel.isLinear.observe(viewLifecycleOwner) {
            if (it) {
                binding.processRec.layoutManager =
                    LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                adapter.setItemViewType(R.layout.item_file_linear)
            } else {
                binding.processRec.layoutManager =
                    GridLayoutManager(requireContext(), 3, RecyclerView.VERTICAL, false)
                adapter.setItemViewType(R.layout.item_file_grid)
            }
        }
        viewModel.cutLiveData.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Error -> Toast.makeText(requireContext(), "error", Toast.LENGTH_SHORT).show()
                is Resource.Finished -> {
                    viewModel.getPath(viewModel.getCurrent())
                    requireActivity().setResult(RESULT_OK)
                    requireActivity().finish()
                }
                is Resource.Loading -> Toast.makeText(requireContext(), "loading", Toast.LENGTH_SHORT).show()
                is Resource.Success -> Toast.makeText(requireContext(), "file ${it.data?.name} completely cut", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.copyLiveData.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Error -> Toast.makeText(requireContext(), "error", Toast.LENGTH_SHORT).show()
                is Resource.Finished -> {
                    viewModel.getPath(viewModel.getCurrent())
                    requireActivity().setResult(RESULT_OK)
                    requireActivity().finish()
                }
                is Resource.Loading -> Toast.makeText(requireContext(), "loading", Toast.LENGTH_SHORT).show()
                is Resource.Success -> Toast.makeText(requireContext(), "file ${it.data?.name} completely copy", Toast.LENGTH_SHORT).show()
            }
        }
    }
}