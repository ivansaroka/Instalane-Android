package com.infinix.instalane.ui.home.checkout.couponLector

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Coupon
import com.infinix.instalane.databinding.FragmentScannedCouponBinding

class ScannedCouponDialogFragment (val coupon: Coupon) : BottomSheetDialogFragment() {

    private val binding by lazy { FragmentScannedCouponBinding.inflate(layoutInflater) }

    lateinit var onAdd: ()->Unit
    lateinit var onCancel: ()->Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.NoDraggingBottomSheetDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.setCanceledOnTouchOutside(false)
        return bottomSheetDialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.mDiscount.text = "INSTA${coupon.discount?.toInt()}%OFF"

        binding.mAddToBasket.setOnClickListener {
            onAdd()
            dismiss()
        }

        binding.mClose.setOnClickListener {
            onCancel()
            dismiss()
        }
    }

}