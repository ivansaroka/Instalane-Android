package com.infinix.instalane.ui.base

import androidx.fragment.app.Fragment
import com.infinix.instalane.R
import com.infinix.instalane.utils.LoaderDialog

open class FragmentAppBase : Fragment() {
    private var mDialog: LoaderDialog? = null

    fun showProgressDialog() {
        if (isAdded) {
            if (mDialog != null && mDialog!!.isShowing)
                hideProgressDialog()
            mDialog = LoaderDialog(requireContext(), R.style.NewDialog)
            mDialog?.show()
        }
    }

    fun hideProgressDialog() {
        if (isAdded) {
            if (mDialog != null && mDialog!!.isShowing)
                mDialog?.dismiss()
        }
    }
}