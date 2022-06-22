package com.android.filemanager.features.main

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.android.filemanager.R
import com.android.filemanager.core.DELETE_FAILED
import com.android.filemanager.core.Resource
import com.android.filemanager.databinding.FragmentProgressDialogBinding
import com.android.filemanager.features.storage.StorageViewModel
import kotlin.properties.Delegates


class ProgressDialogFragment : DialogFragment() {

    private var _binding: FragmentProgressDialogBinding? = null
    private val binding get() = _binding!!
    private var heightDimen: Int = 0
    private val viewModel: StorageViewModel by activityViewModels()

    private var indexNumber by Delegates.vetoable(initialValue = 1) { _, _, newValue ->
        newValue != 0
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        heightDimen = resources.getDimension(com.intuit.sdp.R.dimen._112sdp).toInt()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProgressDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, heightDimen)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_rounded_bg)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, heightDimen)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.dialog_rounded_bg)
    }

    override fun onResume() {
        super.onResume()
        viewModel.delete.observe(viewLifecycleOwner) { itemsTransferred ->
            when (itemsTransferred) {
                is Resource.Loading -> indexNumber = itemsTransferred.data ?: 1
                is Resource.Error -> {
                    if (itemsTransferred.message.equals(DELETE_FAILED)) Toast.makeText(
                        requireContext(),
                        "file with ${itemsTransferred.data} hashCode can not be deleted!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Finished -> {
                    Toast.makeText(requireContext(), getString(R.string.done), Toast.LENGTH_SHORT)
                        .show()
                    dismiss()
                }
                is Resource.Success -> {
                    binding.itemTransferred =
                        ((itemsTransferred.data ?: 1) / (indexNumber ?: 1) * 100).toString()
                }
            }


        }
    }

    companion object {
        fun newInstance(): ProgressDialogFragment {
            val args = Bundle()
            val fragment = ProgressDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }



}