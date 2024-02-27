package com.amirhosseinfsh.core.util

import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.amirhosseinfsh.core.R


fun Context.checkPermissionIsGranted(permission: String): Boolean {
    return ActivityCompat.checkSelfPermission(
        this,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

fun Context.requestPermissionUi(
    dialog: DialogInterface.OnClickListener,
    @StringRes message: Int = R.string.access_storage_prompt,
    @StringRes positiveMess: Int = R.string.yes,
    @StringRes negativeMess: Int = R.string.no
) {
    AlertDialog.Builder(this)
        .setMessage(message)
        .setCancelable(false)
        .setPositiveButton(positiveMess, dialog)
        .setNegativeButton(negativeMess, dialog)
        .show()
}