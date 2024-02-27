package com.amirhosseinfsh.common.base

import android.content.Context
import android.content.Intent
import android.os.Process
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.amirhosseinfsh.common.custom.WaitingDialog
import kotlin.system.exitProcess

abstract class BaseActivity(@LayoutRes private val layoutRes: Int): AppCompatActivity(layoutRes) {


    private var isShow = false

    companion object {
        @JvmStatic var isActive = true
        @JvmStatic var isConfigurationChanged = false
    }

    private val waitingDialog: WaitingDialog by lazy {
        WaitingDialog()
    }

    fun showWaitingDialog() {
        if (!waitingDialog.isAdded && !isShow) {
            isShow = true
            waitingDialog.show(supportFragmentManager, "")
        }
    }

    fun dismissWaitingDialog() {
        if (waitingDialog.isAdded && isShow) {
            waitingDialog.dismiss()
            isShow = false
        }
    }

    protected fun restartApplication() {
        val intent = baseContext.packageManager
            .getLaunchIntentForPackage(
                baseContext.packageName
            )
        intent?.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        Process.killProcess(Process.myPid())
        exitProcess(0)
    }

    fun String.toastMessage() {
        Toast.makeText(this@BaseActivity, this, Toast.LENGTH_SHORT).show()
    }

}