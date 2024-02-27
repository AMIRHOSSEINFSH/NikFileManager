package com.amirhosseinfsh.common.helper

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Environment
import android.os.StatFs
import com.amirhosseinfsh.common.model.volume.StorageVolumeModel
import com.amirhosseinfsh.core.util.EventBus
import com.amirhosseinfsh.core.util.MEMORY
import com.amirhosseinfsh.core.util.USB_CONNECTION_STATUS
import com.amirhosseinfsh.core.util.execute
import com.amirhosseinfsh.core.util.scopeIO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StorageHelper {

    //volume name to status (is exist)
    private val storageStatusFlow: StateFlow<HashMap<MEMORY, Boolean>> = MutableStateFlow(
        hashMapOf()
    )

    private var usbDevice: UsbDevice?= null

    init {
        execute(scopeIO) { checkStorageMap() }
    }

    private suspend fun checkStorageMap() {
        //Internal Memory initial available
        storageStatusFlow.value[MEMORY.INTERNAL] = true

        //Check for Usb ...

        /*EventBus.subscribe<Pair<USB_CONNECTION_STATUS,UsbDevice?>> { itStatus ->
            usbDevice = itStatus.second
            storageStatusFlow.value[MEMORY.USB] = itStatus.first == USB_CONNECTION_STATUS.CONNECTED
        }*/


        //TODO Check for Sd ...
    }

    private fun getAllStoragesVolumeList(): List<StorageVolumeModel> {
        TODO()
    }

    private fun getVolumeOf(storageType: MEMORY): StorageVolumeModel? {
        if (storageStatusFlow.value[storageType] == false) {
            when (storageType) {
                MEMORY.INTERNAL -> {
                    Environment.getDataDirectory()
                }

                MEMORY.USB -> {
                    //UsbManager
                    usbDevice
                    Environment.getDataDirectory()
                }

                MEMORY.SD -> {
                    Environment.getDataDirectory()
                }
            }.let { itFile -> StatFs(itFile.path) }
                .run {
                    StorageVolumeModel(storageType, totalBytes, totalBytes.minus(freeBytes))
                }
        } //else

        //TODO
            return null
    }

}