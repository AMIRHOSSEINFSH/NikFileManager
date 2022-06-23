package com.android.filemanager.features.main

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.android.filemanager.R
import com.android.filemanager.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import android.provider.Settings
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.android.filemanager.core.*
import com.android.filemanager.features.highProcess.CopyCutActivity
import com.android.filemanager.features.storage.StorageViewModel
import kotlin.properties.Delegates


@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {

    private val MANAGE_STORAGE_RC = 201
    private var permissionIsChecked = false
    private var isFirstSelect = true
    private lateinit var viewGroupMargin: ViewGroup.MarginLayoutParams

    private val viewModel: StorageViewModel by viewModels()
    private val pagerAdapter: DemoCollectionAdapter by lazy {
        DemoCollectionAdapter(supportFragmentManager, lifecycle)
    }

    private var upLayoutHeight by Delegates.vetoable(initialValue = 150) { _, oldValue, newValue ->
        newValue != 0 && oldValue <= newValue
    }
    private var downLayoutHeight by Delegates.vetoable(initialValue = 250) { _, oldValue, newValue ->
        newValue != 0 && oldValue <= newValue
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MANAGE_STORAGE_RC) {
            viewModel.permissionGranted()
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    var dialogClickListener =
        DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    permissionIsChecked = true
                    try {
                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        intent.addCategory("android.intent.category.DEFAULT")
                        intent.data = Uri.parse("package:$packageName")
                        startActivityForResult(intent, MANAGE_STORAGE_RC)
                    } catch (e: Exception) {
                        showMessage(e.message ?: getString(R.string.unknownError))
                        val intent = Intent()
                        intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                        startActivityForResult(intent, MANAGE_STORAGE_RC)
                    }
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    finish()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("TAG", "onCreate: viewMo $viewModel")
        viewGroupMargin = binding.viewPager.layoutParams as ViewGroup.MarginLayoutParams
        viewModel.permissionLiveData.observe(this) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !permissionIsChecked && !it) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(R.string.access_storage_prompt).setCancelable(false)
                    .setPositiveButton(R.string.yes, dialogClickListener)
                    .setNegativeButton(R.string.no, dialogClickListener).show()
            } else {
                if (!checkPermission()) {
                    requestPermission()
                }
            }


        }

        setUpListeners()
        setupAnimationSelection()
        setUpObservables()

        binding.viewPager.adapter = pagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager, true, true) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.storage)
                1 -> tab.text = getString(R.string.files)
            }
        }.attach()


    }

    override fun onStart() {
        super.onStart()

    }
    private fun setUpObservables() {
        viewModel.cancelSelection.observe(this) {
            binding.selectAll.tag = SELECTALL
            binding.selectAll.setColorFilter(Color.BLACK, android.graphics.PorterDuff.Mode.SRC_IN)
            isFirstSelect = true
            binding.selectedLayout.setHeightResizeAnimator(
                ANIMATION_TIME_OUT,
                upLayoutHeight,
                0,
                onEnd = { binding.selectedLayout.isVisible = false })

            binding.bottomContainer.setHeightResizeAnimator(
                ANIMATION_TIME_OUT,
                downLayoutHeight,
                0,
                onEnd = { binding.bottomContainer.isVisible = false })
        }
    }

    private fun setUpListeners() {
        val startForResult=
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result: ActivityResult ->
                if (result.resultCode == RESULT_CANCEL) {
                    Toast.makeText(this, "process cancelled", Toast.LENGTH_SHORT).show()
                    viewModel.emitOnClearSelectionList()
                    //  you will get result here in result.data
                    result.data?.getStringExtra("")
                }

            }


        binding.apply {
            cancelSelection.setOnClickListener {

                viewGroupMargin.setMargins(0, 0, 0, 0)
                viewModel.emitOnClearSelectionList()
                selectedLayout.setHeightResizeAnimator(
                    ANIMATION_TIME_OUT,
                    upLayoutHeight,
                    0,
                    onEnd = { selectedLayout.isVisible = false })

                bottomContainer.setHeightResizeAnimator(
                    ANIMATION_TIME_OUT,
                    downLayoutHeight,
                    0,
                    onEnd = { bottomContainer.isVisible = false })
            }

            selectAll.setOnClickListener {

                selectAll.apply {
                    if (tag == SELECTALL) {
                        tag = CLEARALL
                        setColorFilter(Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN)
                        viewModel.emitOnAllViewSelected(false)
                    } else if (tag == CLEARALL) {
                        tag = SELECTALL
                        setImageDrawable(getDrawable(R.drawable.ic_select_all))
                        setColorFilter(Color.BLACK, android.graphics.PorterDuff.Mode.SRC_IN)
                        viewModel.emitOnAllViewSelected(true)
                    }

                }

            }

            ivDelete.setOnClickListener {
                ProgressDialogFragment.newInstance().apply { isCancelable = false }
                    .show(supportFragmentManager, null)
                viewModel.emitOnDelete()
            }

            ivCut.setOnClickListener {
                val intent = Intent(this@MainActivity, CopyCutActivity::class.java)
                intent.putExtra(
                    PROCESS_TYPE,
                    Process.Cut(viewModel.isSelected.value?.map { it.path }.orEmpty())
                )
                startForResult.launch(intent)
            }
            binding.ivCopy.setOnClickListener {
                val intent = Intent(this@MainActivity, CopyCutActivity::class.java)
                intent.putExtra(
                    PROCESS_TYPE,
                    Process.Copy(viewModel.isSelected.value?.map { it.path }.orEmpty())
                )
                startForResult.launch(intent)
            }
        }


    }

    private fun setupAnimationSelection() {
        viewModel.isSelected.observe(this) { isSelected ->
            doOnSelectedLayout(isSelected.size)
            binding.number = isSelected.size.toString()
        }
    }

    private fun doOnSelectedLayout(isSelected: Int) {
        if (isSelected == 1 && isFirstSelect) {

            val dimen = resources.getDimension(com.intuit.sdp.R.dimen._80sdp)
            viewGroupMargin.setMargins(0, 0, 0, dimen.toInt())
            binding.viewPager.requestLayout()
            isFirstSelect = false
            binding.selectedLayout.setHeightResizeAnimator(
                ANIMATION_TIME_OUT,
                0,
                upLayoutHeight,
                onStart = { binding.selectedLayout.isVisible = true },
                onEnd = {
                    upLayoutHeight = binding.selectedLayout.measuredHeight
                })

            binding.bottomContainer.setHeightResizeAnimator(
                ANIMATION_TIME_OUT,
                0,
                downLayoutHeight,
                onStart = { binding.bottomContainer.isVisible = true },
                onEnd = { downLayoutHeight = binding.bottomContainer.measuredHeight })
        } else if (isSelected == 0) {
            viewGroupMargin.setMargins(0, 0, 0, 0)
        }
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return if (result == PackageManager.PERMISSION_GRANTED) {
            true
        } else false
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            Toast.makeText(
                this,
                "Storage permission is requires,please allow from settings",
                Toast.LENGTH_SHORT
            ).show()
        } else ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            111
        )
    }


}