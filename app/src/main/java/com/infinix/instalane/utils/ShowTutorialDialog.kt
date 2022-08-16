package com.infinix.instalane.utils

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.infinix.instalane.R
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.databinding.DialogShowTutorialBinding
import com.infinix.instalane.ui.start.tutorial.TutorialActivity

class ShowTutorialDialog : DialogFragment() {

    private val binding by lazy { DialogShowTutorialBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawableResource(R.color.transparent)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.mOpen.setOnClickListener {
            AppPreferences.setShowTutorial(!binding.mCheckShow.isChecked)
            startActivity(Intent(requireContext(), TutorialActivity::class.java))
            dismiss()
        }
        binding.mSkip.setOnClickListener {
            AppPreferences.setShowTutorial(!binding.mCheckShow.isChecked)
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        //dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val width = (resources.displayMetrics.widthPixels * 0.98).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog!!.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog!!.setCanceledOnTouchOutside(true)
    }

}