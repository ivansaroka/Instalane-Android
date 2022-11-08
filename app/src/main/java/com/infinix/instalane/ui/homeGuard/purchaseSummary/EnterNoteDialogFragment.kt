package com.infinix.instalane.ui.homeGuard.purchaseSummary

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.infinix.instalane.R
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.data.remote.response.Note
import com.infinix.instalane.databinding.FragmentEnterCodeBinding
import com.infinix.instalane.databinding.FragmentEnterNoteBinding
import com.infinix.instalane.utils.listenerAfterTextChanged


class EnterNoteDialogFragment(private val mNote:String?, private val mStatus:Int?) : BottomSheetDialogFragment() {

    private val binding by lazy { FragmentEnterNoteBinding.inflate(layoutInflater) }
    lateinit var onConfirm: (String, Int)->Unit
    var status : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.NoDraggingBottomSheetDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.setCanceledOnTouchOutside(false)

        bottomSheetDialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
        }

        return bottomSheetDialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val user = AppPreferences.getUser()
        binding.mUsername.text = user?.fullname

        binding.mStatus1.setOnClickListener {
            status = Note.ALL_SCANNED_PAID
            binding.mStatus1.alpha = 1f
            binding.mStatus2.alpha = 0.5f
            binding.mStatus3.alpha = 0.5f
            binding.mConfirm.isEnabled = true
        }

        binding.mStatus2.setOnClickListener {
            status = Note.ONE_ITEM_NOT_SCANNED_PAID
            binding.mStatus1.alpha = 0.5f
            binding.mStatus2.alpha = 1f
            binding.mStatus3.alpha = 0.5f
            binding.mConfirm.isEnabled = binding.mNote.text.toString().isNotEmpty()
        }

        binding.mStatus3.setOnClickListener {
            status = Note.MORE_ITEM_NOT_SCANNED_PAID
            binding.mStatus1.alpha = 0.5f
            binding.mStatus2.alpha = 0.5f
            binding.mStatus3.alpha = 1f
            binding.mConfirm.isEnabled = binding.mNote.text.toString().isNotEmpty()
        }

        binding.mNote.listenerAfterTextChanged {
            binding.mConfirm.isEnabled = it.isNotEmpty() && status!=null
            if (status==Note.ALL_SCANNED_PAID)
                binding.mConfirm.isEnabled = true
        }

        binding.mConfirm.setOnClickListener {
            onConfirm(binding.mNote.text.toString(), status!!)
            dismiss()
        }

        binding.mClose.setOnClickListener { dismiss() }

        if (!mNote.isNullOrEmpty())
            binding.mNote.setText(mNote)

        if (mStatus!=null){
            status = mStatus

            binding.mStatus1.alpha = 0.5f
            binding.mStatus2.alpha = 0.5f
            binding.mStatus3.alpha = 0.5f

            when(status) {
                Note.ALL_SCANNED_PAID -> binding.mStatus1.alpha = 1f
                Note.ONE_ITEM_NOT_SCANNED_PAID-> binding.mStatus2.alpha = 1f
                Note.MORE_ITEM_NOT_SCANNED_PAID-> binding.mStatus3.alpha = 1f
            }
        }
    }

}