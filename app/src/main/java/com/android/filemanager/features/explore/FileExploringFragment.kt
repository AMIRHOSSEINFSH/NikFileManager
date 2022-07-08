package com.android.filemanager.features.explore

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.filemanager.R
import com.android.filemanager.core.ActionOnList
import com.android.filemanager.core.BaseFragment
import com.android.filemanager.core.StorageHelper
import com.android.filemanager.core.UNKNOWN_ERROR
import com.android.filemanager.databinding.FragmentFileExploringBinding
import com.android.filemanager.features.files.FileListAdapter
import com.android.filemanager.features.storage.StorageViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


@AndroidEntryPoint
class FileExploringFragment :
    BaseFragment<FragmentFileExploringBinding>(R.layout.fragment_file_exploring) {

    private val viewModel: StorageViewModel by activityViewModels()
    private val args: FileExploringFragmentArgs by navArgs()

    private var currentPath: String? = null

    private val fileExploreAdapter by lazy {
        FileListAdapter(itemClicked = { fileModel ->
            val shouldNavigate = viewModel.isSelected.value?.isEmpty() ?: true
            if (fileModel.isDirectory) {
                viewModel.emitOnParentPath(fileModel.path)
                if (shouldNavigate) {
                    viewModel.doOnDataList(ActionOnList.ADD(fileModel.path))
                    //viewModel.addNewPath(fileModel.name)
                    findNavController().navigate(
                        FileExploringFragmentDirections.actionFileExploringFragmentSelf()
                            .setPath(fileModel.path)
                    )
                }
            } else {
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
            shouldNavigate
        }, itemLongClicked = {
            viewModel.emitOnViewSelected(it)
        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSetUp()
        setUpAdapters()
        setUpObservables()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!viewModel.processIsWorking) {
                        //todo if backQueue.size is root so:
                        if (findNavController().backQueue.size != 2) {
                            //todo if you are not in root && there are some items that checked it will clear it instead of pop and back navigate
                            if (viewModel.isSelected.value!!.isNotEmpty()) {
                                viewModel.emitOnAllViewSelected(true)
                            }
                            //todo you are not in root and there is no selected item so just need to pop and back navigate
                            else {
                                //viewModel.popLastPath()
                                // viewModel.setAndPushLastObjInStack(args.path)
                                viewModel.doOnDataList(ActionOnList.POP(null))
                                findNavController().popBackStack()
                            }
                        }
                        //todo you are in root and just need to remove selection
                        else {
                            viewModel.emitOnAllViewSelected(true)
                        }
                    }
                }

            }
        )

    }

    private fun initSetUp() {
        if (viewModel.isFirst) {
            viewModel.isFirst = false
            currentPath = args.path
            viewModel.setRootOfLastStack(currentPath)
        }
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

            if (!viewModel.fileInputIsLock) {
                viewModel.fileInputIsLock = true
                if (list.isNotEmpty())
                    viewModel.emitOnParentPath(list.first().parent)
                fileExploreAdapter.submitList(list)
            }
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
        viewModel.shouldClearSelection.observe(viewLifecycleOwner) { shouldCLear ->
            if (shouldCLear) fileExploreAdapter.clearSelection() else fileExploreAdapter.selectAll()
        }
    }

}