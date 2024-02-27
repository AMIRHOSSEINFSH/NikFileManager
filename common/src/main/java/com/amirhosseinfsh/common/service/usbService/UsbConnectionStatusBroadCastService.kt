package com.amirhosseinfsh.common.service.usbService

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import com.amirhosseinfsh.core.util.EventBus
import com.amirhosseinfsh.core.util.USB_CONNECTION_STATUS
import timber.log.Timber


class UsbConnectionStatusBroadCastService: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action
        if (UsbManager.ACTION_USB_DEVICE_ATTACHED == action) {
            Timber.tag("PERMISSION").i("BroadcastReceiver USB Connected")
            val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
            EventBus.publish(Pair(USB_CONNECTION_STATUS.CONNECTED,device))
        } else if (UsbManager.ACTION_USB_DEVICE_DETACHED == action) {
            val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
            Timber.tag("PERMISSION").i("BroadcastReceiver USB Disconnected")
            EventBus.publish(Pair(USB_CONNECTION_STATUS.DISCONNECTED,device))
        }

    }
}