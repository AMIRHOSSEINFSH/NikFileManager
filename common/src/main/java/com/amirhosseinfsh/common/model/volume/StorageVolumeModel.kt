package com.amirhosseinfsh.common.model.volume

import com.amirhosseinfsh.core.util.MEMORY

data class StorageVolumeModel(
    val storageType: MEMORY,
    val totalSize: Long,
    val inUsedSize: Long
)