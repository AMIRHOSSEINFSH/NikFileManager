package com.android.filemanager.features.splashActivity

import android.Manifest
import android.animation.Animator
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.amirhosseinfsh.common.base.BaseActivity
import com.amirhosseinfsh.core.util.PERMISSION_CODE
import com.amirhosseinfsh.core.util.checkPermissionIsGranted
import com.amirhosseinfsh.core.util.requestPermissionUi
import com.amirhosseinfsh.viewBinding.viewBinding
import com.android.filemanager.R
import com.android.filemanager.databinding.ActivitySplashBinding
import com.android.filemanager.features.main.MainActivity

class SplashActivity : BaseActivity(R.layout.activity_splash) {

    private val binding: ActivitySplashBinding by viewBinding()

    private val isFullAccessStorageGranted
        get() = Build.VERSION.SDK_INT < Build.VERSION_CODES.R || Environment.isExternalStorageManager()
    private val isWritePermissionGranted
        get() = checkPermissionIsGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var animationIsEnded = false

    @RequiresApi(Build.VERSION_CODES.R)
    var dialogClickListener =
        DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    try {
                        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        intent.addCategory("android.intent.category.DEFAULT")
                        intent.data = Uri.parse("package:$packageName")
                        startActivityForResult(intent, PERMISSION_CODE.MANAGE_STORAGE_RC.invoke())
                    } catch (e: Exception) {
                        (e.message ?: getString(R.string.unknownError)).toastMessage()
                        val intent = Intent()
                        intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                        startActivityForResult(intent, PERMISSION_CODE.MANAGE_STORAGE_RC.invoke())
                    }
                }

                DialogInterface.BUTTON_NEGATIVE -> {
                    finish()
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

            .also { setUpRecheckPermissionGranted() }

        binding.lottieAnim.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {

            }

            override fun onAnimationEnd(p0: Animator) {
                animationIsEnded = true
                launchActivity()
            }

            override fun onAnimationCancel(p0: Animator) {

            }

            override fun onAnimationRepeat(p0: Animator) {

            }

        })

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.firstOrNull() == 0) {
            when (PERMISSION_CODE.getPermissionFromCode(requestCode)) {
                PERMISSION_CODE.MANAGE_STORAGE_RC -> {
                    launchActivity()
                }

                PERMISSION_CODE.WIRE_STORAGE_PM -> {
                    if (!isFullAccessStorageGranted) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        requestPermissionUi(dialogClickListener)
                    } else launchActivity()
                }

                null -> {

                }
            }
        } else {
            finish()
        }
    }

    private fun setUpRecheckPermissionGranted() {
        when (isWritePermissionGranted) {
            true -> {
                if (isFullAccessStorageGranted) {
                    launchActivity()
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        requestPermissionUi(dialogClickListener)
                    }
                }

            }

            false -> {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_CODE.WIRE_STORAGE_PM.invoke()
                )
            }
        }
    }

    private fun launchActivity() {
        if (isFullAccessStorageGranted && animationIsEnded && isWritePermissionGranted) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 0) {
            when (PERMISSION_CODE.getPermissionFromCode(requestCode)) {
                PERMISSION_CODE.MANAGE_STORAGE_RC -> {
                    launchActivity()
                }

                PERMISSION_CODE.WIRE_STORAGE_PM -> {
                    if (!isFullAccessStorageGranted) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        requestPermissionUi(dialogClickListener)
                    } else launchActivity()
                }

                null -> {

                }
            }
        } else {
            finish()
        }


    }
}