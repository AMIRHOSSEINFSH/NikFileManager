package com.android.filemanager.features.files

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.android.filemanager.R
import com.android.filemanager.core.BaseFragment
import com.android.filemanager.core.PATH_CREATE_FOLDER
import com.android.filemanager.databinding.FragmentCreateFileBinding
import com.android.filemanager.features.storage.StorageViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreateFileFragment constructor(private val onSubmit: (() -> Unit)? = null) :
    DialogFragment() {

    private var _binding: FragmentCreateFileBinding? = null
    private val binding: FragmentCreateFileBinding get() = _binding!!
    private val viewModel: StorageViewModel by activityViewModels()
    private lateinit var path: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val path = arguments?.getString(PATH_CREATE_FOLDER)
        if (path == null) dismiss()
        else this.path = path
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_rounded_bg)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateFileBinding.inflate(inflater, container, false)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_rounded_bg)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.submit.setOnClickListener {
            val txt = binding.fileNameEt.text.toString()
            if (txt.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.nullNameError),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val isSuccess = viewModel.createFolder(path, txt)
                if (isSuccess) {
                    onSubmit?.invoke()
                    viewModel.getPath(viewModel.parentPathLiveData.value)
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.folderSucessFull),
                        Toast.LENGTH_SHORT
                    ).show()
                } else Toast.makeText(
                    requireContext(),
                    getString(R.string.folderCreateFailed),
                    Toast.LENGTH_SHORT
                ).show()
                dismiss()
            }
        }

        binding.cancel.setOnClickListener {
            dismiss()
        }
    }


    companion object {
        fun newInstance(path: String?,onSubmit: (() -> Unit)?=null): CreateFileFragment {
            val args = Bundle()
            args.putString(PATH_CREATE_FOLDER, path)
            val fragment = CreateFileFragment(onSubmit)
            fragment.arguments = args
            return fragment
        }
    }

}