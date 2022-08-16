package com.infinix.instalane.utils

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.StyleRes
import com.infinix.instalane.R

class LoaderDialog : Dialog {
    constructor(context: Context) : super(context) {
        initView(context)
    }
    constructor(context: Context, @StyleRes themeResId: Int) : super(context, themeResId) {
        initView(context)
    }
    private fun initView(context: Context) {
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_loader)
        window!!.setBackgroundDrawableResource(R.color.transparent)
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }
}
