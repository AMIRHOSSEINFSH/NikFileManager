package com.android.filemanager.features.main

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.amirhosseinfsh.common.base.BaseActivity
import com.amirhosseinfsh.common.base.LazyBottomNavFragmentAdapter
import com.amirhosseinfsh.common.service.usbService.UsbConnectionStatusBroadCastService
import com.amirhosseinfsh.core.util.ACTION_USB_PERMISSION
import com.amirhosseinfsh.core.util.EventBus
import com.amirhosseinfsh.core.util.USB_CONNECTION_STATUS
import com.amirhosseinfsh.core.util.scopeIO
import com.amirhosseinfsh.viewBinding.viewBinding
import com.android.filemanager.R
import com.android.filemanager.databinding.ActivityMainBinding
import com.android.filemanager.features.files.FilesFragment
import com.android.filemanager.features.storage.StorageFragment
import com.android.filemanager.features.storage.StorageViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File


@AndroidEntryPoint
class MainActivity : BaseActivity(R.layout.activity_main) {

    private val binding: ActivityMainBinding by viewBinding()

    private val storageViewModel: StorageViewModel by viewModels()
    
    private val fileManagerAdapter: LazyBottomNavFragmentAdapter by lazy { 
        LazyBottomNavFragmentAdapter(this, arrayListOf(storageFragment,filesFragment))
    }

    private val storageFragment: StorageFragment by lazy {
        StorageFragment()
    }
    
    private val filesFragment: FilesFragment by lazy { 
        FilesFragment()
    }

    private val usbManager: UsbManager by lazy {
        getSystemService(Context.USB_SERVICE) as UsbManager
    }

    private val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"

    private val usbReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (ACTION_USB_PERMISSION == intent.action) {
                synchronized(this) {
                    val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        device?.apply {
                            // call method to set up device communication
                            EventBus.publish(this)
                        }
                    } else {
                       // Log.d(TAG, "permission denied for device $device")
                    }
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)





            //setUpViewpager()
            //.also {
                setUpUsbPermissionBroadCast()
                scopeIO.launch {
                    EventBus.subscribe<Pair<USB_CONNECTION_STATUS,UsbDevice?>> { itStatus ->
                        val permissionIntent = PendingIntent.getBroadcast(this@MainActivity, 0, Intent(ACTION_USB_PERMISSION), FLAG_IMMUTABLE)
                        usbManager.requestPermission(itStatus.second,permissionIntent)
                        // usbDevice = itStatus.second
                        // storageStatusFlow.value[MEMORY.USB] = itStatus.first == USB_CONNECTION_STATUS.CONNECTED
                    }
                }
        scopeIO.launch {
            delay(7000)
            File("/mnt").list()?.onEach {
                val list = File(it).list()
                list
            }
        }
          //  }
    }

    private fun setUpUsbPermissionBroadCast() {
        val filter = IntentFilter()
        val receiver=  UsbConnectionStatusBroadCastService()
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        registerReceiver(receiver, filter)
        scopeIO.launch {
            EventBus.subscribe<USB_CONNECTION_STATUS> { itStatus->
                binding.crdUsbConnection.isVisible = itStatus == USB_CONNECTION_STATUS.CONNECTED
            }
        }

    }

    private fun setUpViewpager() {
        binding.viewPager.adapter = fileManagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager, true, true) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.storage)
                1 -> tab.text = getString(R.string.files)
            }
        }.attach()
    }



}