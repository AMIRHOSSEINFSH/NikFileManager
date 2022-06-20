package com.android.filemanager.core

import androidx.databinding.ViewDataBinding

interface BaseView<T: ViewDataBinding> {

    var _binding: T?
    val binding: T get() = _binding!!

    fun showMessage(message: String)

}