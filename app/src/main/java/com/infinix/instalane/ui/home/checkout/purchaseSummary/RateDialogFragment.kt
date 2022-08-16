package com.infinix.instalane.ui.home.checkout.purchaseSummary

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.infinix.instalane.R
import com.infinix.instalane.databinding.FragmentRateBinding
import com.infinix.instalane.utils.listenerAfterTextChanged


class RateDialogFragment () : BottomSheetDialogFragment() {

    private val binding by lazy { FragmentRateBinding.inflate(layoutInflater) }

    lateinit var onConfirm : (Int, String)->Unit
    lateinit var onClose : ()->Unit
    var rateSelected :Int? = null

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

        binding.mComment.listenerAfterTextChanged { binding.mConfirm.isEnabled = it.isNotEmpty() && rateSelected!=null }
        binding.mConfirm.setOnClickListener {
            onConfirm(rateSelected!!, binding.mComment.text.toString())
            dismiss()
        }
        binding.mClose.setOnClickListener {
            onClose()
            dismiss()
        }
        binding.mRate1.setOnClickListener { unselectAll(binding.mRate1, 1) }
        binding.mRate2.setOnClickListener { unselectAll(binding.mRate2, 2) }
        binding.mRate3.setOnClickListener { unselectAll(binding.mRate3, 3) }
        binding.mRate4.setOnClickListener { unselectAll(binding.mRate4, 4) }
        binding.mRate5.setOnClickListener { unselectAll(binding.mRate5, 5) }

        unselectState()
    }

    private fun unselectAll(selectItem : ImageView, rate:Int){
        unselectState()

        selectItem.setBackgroundResource(R.drawable.circle_orange)
        rateSelected = rate
        binding.mConfirm.isEnabled =  binding.mComment.text.toString().isNotEmpty()

        when(rate){
            1 ->{
                binding.mRate1.setBackgroundResource(R.drawable.circle_orange)
            }
            2 ->{
                binding.mRate1.setBackgroundResource(R.drawable.circle_orange)
                binding.mRate2.setBackgroundResource(R.drawable.circle_orange)
            }
            3 ->{
                binding.mRate1.setBackgroundResource(R.drawable.circle_orange)
                binding.mRate2.setBackgroundResource(R.drawable.circle_orange)
                binding.mRate3.setBackgroundResource(R.drawable.circle_orange)
            }
            4 ->{
                binding.mRate1.setBackgroundResource(R.drawable.circle_orange)
                binding.mRate2.setBackgroundResource(R.drawable.circle_orange)
                binding.mRate3.setBackgroundResource(R.drawable.circle_orange)
                binding.mRate4.setBackgroundResource(R.drawable.circle_orange)
            }
            5 ->{
                binding.mRate1.setBackgroundResource(R.drawable.circle_orange)
                binding.mRate2.setBackgroundResource(R.drawable.circle_orange)
                binding.mRate3.setBackgroundResource(R.drawable.circle_orange)
                binding.mRate4.setBackgroundResource(R.drawable.circle_orange)
                binding.mRate5.setBackgroundResource(R.drawable.circle_orange)
            }
        }
    }

    private fun unselectState() {
        binding.mRate1.setBackgroundResource(R.drawable.circle_gray)
        binding.mRate2.setBackgroundResource(R.drawable.circle_gray)
        binding.mRate3.setBackgroundResource(R.drawable.circle_gray)
        binding.mRate4.setBackgroundResource(R.drawable.circle_gray)
        binding.mRate5.setBackgroundResource(R.drawable.circle_gray)
    }

}