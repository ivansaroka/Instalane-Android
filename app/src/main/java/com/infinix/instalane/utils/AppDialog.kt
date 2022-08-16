package com.infinix.instalane.utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.infinix.instalane.R

class AppDialog private constructor(
    context: Context,
    title: String?=null,
    body: CharSequence?=null,
    confirm: String?=null,
    cancel: String?=null,
    isCancelable: Boolean?=null,
    confirmListener: ConfirmListener?=null,
    cancelListener: CancelListener? = null
) : AlertDialog.Builder(context) {

    private val title: String?
    private val confirm: String?
    private val cancel: String?
    private val body: CharSequence?
    private var confirmListener: ConfirmListener?
    private val cancelListener: CancelListener?
    private var isConfirm = false
    private var isCancelable = true

    private lateinit var alertDialog: AlertDialog

    init {
        this.title = title
        this.body = body
        this.confirm = confirm
        this.cancel = cancel
        this.confirmListener = confirmListener
        this.cancelListener = cancelListener
        this.isCancelable = isCancelable ?: true
        show()
    }

    fun setConfirmListener(confirmListener: ConfirmListener?) {
        this.confirmListener = confirmListener
    }



    @SuppressLint("InflateParams")
    override fun show(): AlertDialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_custom, null)
        val titleTextView = view.findViewById<TextView>(R.id.title)
        val bodyTextView = view.findViewById<TextView>(R.id.body)
        val cancelButton: AppCompatButton = view.findViewById(R.id.cancel)
        val confirmButton: AppCompatButton = view.findViewById(R.id.confirm)
        if (title == null || title.isEmpty()) titleTextView.visibility = View.GONE else {
            titleTextView.visibility = View.VISIBLE
            titleTextView.text = Html.fromHtml(title)
        }
        bodyTextView.text = body
        setView(view)
        alertDialog = super.show()
        if (confirm != null) {
            confirmButton.text = confirm
        }
        alertDialog.setCancelable(isCancelable)
        alertDialog.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.setOnDismissListener(DialogInterface.OnDismissListener { if (cancelListener != null && !isConfirm) cancelListener.onCancel() })
        if (cancel != null) {
            cancelButton.text = cancel
            cancelButton.visibility = View.VISIBLE
        }else
            cancelButton.visibility = View.GONE
        confirmButton.setOnClickListener {
            isConfirm = true
            if (confirmListener != null) confirmListener!!.onClick()
            alertDialog.dismiss()
        }
        cancelButton.setOnClickListener {
            isConfirm = false
            cancelListener?.onCancel()
            alertDialog.dismiss()
        }
        val container = alertDialog.findViewById<View>(R.id.mBackgroundTheme)
        animateEntrance(container)
        return alertDialog
    }

    override fun setCancelable(cancelable: Boolean): AlertDialog.Builder {
        return super.setCancelable(cancelable)
    }

    private fun animateEntrance(container: View) {
        container.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                container.viewTreeObserver.removeOnGlobalLayoutListener(this)
                container.translationY = container.height.toFloat()
                container.alpha = 0f
                container.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setInterpolator(JellyInterpolator())
                    .setDuration(300)
                    .start()
            }
        })
    }

    interface ConfirmListener {
        fun onClick()
    }

    interface CancelListener {
        fun onCancel()
    }

    companion object {
        fun showDialog(
            context: Context,
            title: String?=null,
            body: CharSequence?=null,
            confirm: String?=null,
            cancel: String?=null,
            isCancelable: Boolean?=null,
            confirmListener: ConfirmListener?=null,
            cancelListener: CancelListener?=null
        ) {
            AppDialog(
                context,
                title,
                body,
                confirm,
                cancel,
                isCancelable,
                confirmListener,
                cancelListener
            )
        }
    }


}