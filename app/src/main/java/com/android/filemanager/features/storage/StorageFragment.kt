package com.android.filemanager.features.storage

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getExternalFilesDirs
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amirhosseinfsh.common.base.BaseFragment
import com.amirhosseinfsh.viewBinding.viewBinding
import com.android.filemanager.R
import com.android.filemanager.core.SHOW_MIMETYPE
import com.android.filemanager.core.StorageHelper
import com.android.filemanager.core.UNKNOWN_ERROR
import com.android.filemanager.databinding.FragmentStorageBinding
import com.android.filemanager.features.mimType.MimTypesActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File


@AndroidEntryPoint
class StorageFragment : BaseFragment(R.layout.fragment_storage) {

    private val binding: FragmentStorageBinding by viewBinding(FragmentStorageBinding::bind)

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
                (e.message ?: UNKNOWN_ERROR)
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

    val SD_CARD = "sdCard"
    val EXTERNAL_SD_CARD = "externalSdCard"
    private val ENV_SECONDARY_STORAGE = "SECONDARY_STORAGE"

    fun getAllStorageLocations(): Map<String, File>? {
        val storageLocations: MutableMap<String, File> = HashMap(10)
        val sdCard = Environment.getExternalStorageDirectory()
        storageLocations[SD_CARD] = sdCard
        val rawSecondaryStorage = System.getenv(ENV_SECONDARY_STORAGE)
        if (!TextUtils.isEmpty(rawSecondaryStorage)) {
            val externalCards =
                rawSecondaryStorage.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            for (i in externalCards.indices) {
                val path = externalCards[i]
                storageLocations[EXTERNAL_SD_CARD + String.format(if (i == 0) "" else "_%d", i)] =
                    File(path)
            }
        }
        return storageLocations
    }
    private fun initSetUp() {


        val d =getExternalFilesDirs(requireContext(),null)
        //val dire = Sto.getAllStorages(requireContext())
        val f = getExternalFilesDirs(requireContext(), null)
        for (i in f.indices) {
            val path: String? =
                f[i].parent?.replace("/Android/data/", "")?.replace(requireContext().packageName, "")
            Timber.tag("DIRS").i(path)
        }

        val manager = requireContext().getSystemService(Context.USB_SERVICE) as UsbManager
        val deviceList= manager.deviceList
        val acce = manager.accessoryList

        val nameList = getExternalFilesDirs(requireContext(),null).first().list()
        val remo = Environment.isExternalStorageRemovable()
        val emulated=  Environment.isExternalStorageEmulated()
        val externalStorage: String = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED_READ_ONLY == externalStorage) {
            //do anything
        } else {
            // show error message
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val state=  Environment.getStorageDirectory()
            val legacy=  Environment.isExternalStorageLegacy()
            val manager = Environment.isExternalStorageManager()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            val names = MediaStore.getExternalVolumeNames(requireContext())
            Timber.tag("VOLUME_NAMES").i(names.toString())
        }
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