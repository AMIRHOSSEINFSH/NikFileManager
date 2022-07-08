package com.android.filemanager.features.mimType

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.filemanager.R
import com.android.filemanager.components.getLongValue
import com.android.filemanager.components.getStringValue
import com.android.filemanager.core.*
import com.android.filemanager.databinding.ActivityMimTypesBinding
import com.android.filemanager.features.files.FileListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.File
import java.util.*

@AndroidEntryPoint
class MimTypesActivity : BaseActivity<ActivityMimTypesBinding>(R.layout.activity_mim_types) {


    private val viewModel: MimTypesViewModel by viewModels()
    private var currentMimeType = ""
    private val job = Job()
    private val adapter: FileListAdapter by lazy {
        FileListAdapter(R.layout.item_file_grid, itemClicked = { fileModel ->
            try {
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                intent.type = StorageHelper.getMimeType(fileModel.path)
                intent.data = FileProvider.getUriForFile(
                    this,
                    this.applicationContext.packageName + ".provider",
                    fileModel
                )
                startActivity(intent)
            } catch (e: Exception) {
                showMessage(e.message ?: UNKNOWN_ERROR)
            }
            true
        },
            itemLongClicked = {

            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentMimeType =
            (intent.getSerializableExtra(SHOW_MIMETYPE) as StorageHelper.MimTypes).value

        setUpAdapters()

        lifecycleScope.launch(Dispatchers.Default + job) {

            getProperFileDirItems { files ->
                adapter.submitList(files)
            }
        }
    }


    private suspend fun getProperFileDirItems(callback: CoroutineScope.(ArrayList<File>) -> Unit) {
        val fileDirItems = ArrayList<File>()
        val uri = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.DATE_MODIFIED
        )

        try {
            queryCursor(uri, projection) { cursor ->
                try {
                    val fullMimetype =
                        cursor.getStringValue(MediaStore.Files.FileColumns.MIME_TYPE)?.lowercase(
                            Locale.getDefault()
                        ) ?: return@queryCursor
                    val name = cursor.getStringValue(MediaStore.Files.FileColumns.DISPLAY_NAME)
                    if (name.startsWith(".")) {
                        return@queryCursor
                    }

                    val size = cursor.getLongValue(MediaStore.Files.FileColumns.SIZE)
                    if (size == 0L) {
                        return@queryCursor
                    }

                    val path = cursor.getStringValue(MediaStore.Files.FileColumns.DATA)
                    val lastModified =
                        cursor.getLongValue(MediaStore.Files.FileColumns.DATE_MODIFIED) * 1000

                    val mimetype = fullMimetype.substringBefore("/")
                    when (currentMimeType) {
                        IMAGES -> {
                            if (mimetype == "image") {
                                fileDirItems.add(File(path))
                            }
                        }
                        VIDEOS -> {
                            if (mimetype == "video") {
                                fileDirItems.add(File(path))
                            }
                        }
                        AUDIO -> {
                            if (mimetype == "audio" || extraAudioMimeTypes.contains(fullMimetype)) {
                                fileDirItems.add(File(path))
                            }
                        }
                        DOCUMENTS -> {
                            if (mimetype == "text" || extraDocumentMimeTypes.contains(fullMimetype)) {
                                fileDirItems.add(File(path))
                            }
                        }
                        ARCHIVES -> {
                            if (archiveMimeTypes.contains(fullMimetype)) {
                                fileDirItems.add(File(path))
                            }
                        }
                        OTHERS -> {
                            if (mimetype != "image" && mimetype != "video" && mimetype != "audio" && mimetype != "text" &&
                                !extraAudioMimeTypes.contains(fullMimetype) && !extraDocumentMimeTypes.contains(
                                    fullMimetype
                                ) &&
                                !archiveMimeTypes.contains(fullMimetype)
                            ) {
                                fileDirItems.add(File(path))
                            }
                        }
                    }
                } catch (e: Exception) {
                }
            }
        } catch (e: Exception) {
            showMessage(e.message ?: UNKNOWN_ERROR)
        }

        withContext(Dispatchers.Main) {
            callback(fileDirItems)
        }
    }

    private fun setUpAdapters() {
        binding.mimTypesShowRec.layoutManager =
            GridLayoutManager(this, 3, RecyclerView.VERTICAL, false)
        binding.mimTypesShowRec.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }


}

