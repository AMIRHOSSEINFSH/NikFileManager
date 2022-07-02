package com.android.filemanager.features.category

import android.os.Bundle
import android.os.Environment
import androidx.activity.viewModels
import com.android.filemanager.R
import com.android.filemanager.core.BaseActivity
import com.android.filemanager.core.DIRECTORY_CATEGORY
import com.android.filemanager.databinding.ActivityCategoryBinding
import com.android.filemanager.features.files.FileListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryActivity : BaseActivity<ActivityCategoryBinding>(R.layout.activity_category) {

    private val viewModel: CategoryViewModel by viewModels()
    private val adapter: FileListAdapter by lazy {
        FileListAdapter(itemClicked = { true }, itemLongClicked = {})
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val data = intent.getStringExtra(DIRECTORY_CATEGORY)
        if (!data.isNullOrEmpty()) viewModel.setCategory(data)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initSetUp()
        setUpObservables()

    }

    private fun initSetUp() {
        viewModel.getPath(null)
    }

    private fun setUpObservables() {
        viewModel.listLiveData.observe(this) { list ->
            adapter.submitList(list)
        }
    }


}