package com.amirhosseinfsh.core.util

import androidx.annotation.StringRes
import com.amirhosseinfsh.core.R

enum class PERMISSION_CODE (private val requestCode: Int){
    MANAGE_STORAGE_RC(1001),
    WIRE_STORAGE_PM(1002);
    companion object {
        fun getPermissionFromCode(code: Int): PERMISSION_CODE? {
            return entries.find { it.requestCode == code }
        }
    }
    fun invoke() = requestCode
}

enum class USB_CONNECTION_STATUS {
    CONNECTED,
    DISCONNECTED
}

enum class MEMORY(@StringRes private val memNameRef: Int) {
    INTERNAL(R.string.internal),
    USB(R.string.usb),
    SD(R.string.sd)
}


