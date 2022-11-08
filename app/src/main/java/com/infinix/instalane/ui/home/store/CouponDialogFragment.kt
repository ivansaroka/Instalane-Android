package com.infinix.instalane.ui.home.store

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Coupon
import com.infinix.instalane.databinding.FragmentCouponBinding
import com.infinix.instalane.utils.DateUtils


class CouponDialogFragment (val coupon: Coupon) : BottomSheetDialogFragment() {

    private val binding by lazy { FragmentCouponBinding.inflate(layoutInflater) }

    lateinit var onUse: ()->Unit

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

        binding.mCoupon.text = coupon.code
        binding.mTitle.text = "${coupon.discount?.toInt()}% off"
        binding.mDate.text = ""
        if (coupon.finishTime!=null){
            val sExpire = DateUtils().convertDate(coupon.finishTime, DateUtils.FORMAT_EXPIRED_API, DateUtils.FORMAT_EXPIRED)
            binding.mDate.text = sExpire
        }

        binding.mCopy.setOnClickListener {
            val clipboard: ClipboardManager? = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
            val clip = ClipData.newPlainText("COUPON_CODE", coupon.code)
            clipboard?.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }

        binding.mUseCoupon.setOnClickListener {
            onUse()
            dismiss()
        }

        binding.mClose.setOnClickListener {
            dismiss()
        }
    }

}