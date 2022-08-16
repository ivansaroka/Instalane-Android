package com.infinix.instalane.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.infinix.instalane.R

class ChoosePhotoDialog : DialogFragment() {

    lateinit var onCamera:()->Unit
    lateinit var onGallery:()->Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawableResource(R.color.transparent)
        return inflater.inflate(R.layout.dialog_choose_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.mCamera).setOnClickListener {
            onCamera()
            dismiss()
        }
        view.findViewById<View>(R.id.mGallery).setOnClickListener {
            onGallery()
            dismiss()
        }
    }

}