package com.amirhosseinfsh.common.base



import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.amirhosseinfsh.common.custom.WaitingDialog


abstract class BaseFragment(@LayoutRes val layout:Int) : Fragment(layout) {
    private var isShow = false

    protected var mIsVisible: Boolean = false

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        mIsVisible = menuVisible
    }

    private val waitingDialog: WaitingDialog by lazy {
        WaitingDialog()
    }

    /*val operator: AnimationViewOperator by lazy {
        AnimationViewOperator(Interpolators(Easings.CIRC_OUT))
    }*/

    fun showWaitingDialog(canCancel: Boolean = true) {
        if (!waitingDialog.isAdded && !isShow) {
            isShow = true
            waitingDialog.apply { isCancelable = canCancel }
                .show(requireActivity().supportFragmentManager, "")
        }
    }

    fun dismissWaitingDialog() {
        if (waitingDialog.isAdded && isShow) {
            waitingDialog.dismiss()
            isShow = false
        }
    }

    fun String.toastMessage() {
        Toast.makeText(requireContext(), this, Toast.LENGTH_SHORT).show()
    }
}