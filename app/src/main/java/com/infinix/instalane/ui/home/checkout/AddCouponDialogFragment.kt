package com.infinix.instalane.ui.home.checkout

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Coupon
import com.infinix.instalane.data.remote.response.Store
import com.infinix.instalane.databinding.FragmentWriteCouponBinding
import com.infinix.instalane.ui.home.checkout.couponLector.CouponLectorActivity
import com.infinix.instalane.ui.home.checkout.couponLector.CouponViewModel
import com.infinix.instalane.utils.listenerAfterTextChanged


class AddCouponDialogFragment (val mStore: Store) : BottomSheetDialogFragment() {

    private val viewModel by lazy {
        ViewModelProvider(this)[CouponViewModel::class.java].apply {
            couponLiveData.observe(this@AddCouponDialogFragment, this@AddCouponDialogFragment::showScannedCoupon)
            onError.observe(this@AddCouponDialogFragment) {
                binding.mConfirm.visibility = View.VISIBLE
                binding.mProgress.visibility = View.GONE
            }
        }
    }

    private val binding by lazy { FragmentWriteCouponBinding.inflate(layoutInflater) }
    lateinit var onConfirm: (Coupon, mustShowConfirmation : Boolean)->Unit
    private var mCoupon:Coupon?=null

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

        binding.mCode.listenerAfterTextChanged { binding.mConfirm.isEnabled = it.isNotEmpty() }
        binding.mBarCode.setOnClickListener { openCouponCamera() }
        binding.mConfirm.setOnClickListener {
            binding.mConfirm.visibility = View.GONE
            binding.mProgress.visibility = View.VISIBLE
            viewModel.getCoupon(mStore.id!!, binding.mCode.text.toString())
        }
        binding.mClose.setOnClickListener { dismiss() }
    }

    private fun showScannedCoupon(coupon: Coupon){
        onConfirm.invoke(coupon, true)
        dismiss()
    }

    private fun openCouponCamera() {
        val intent = CouponLectorActivity.getIntent(requireContext(), mStore)
        resultLauncher.launch(intent)
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data!=null && result.data!!.hasExtra("COUPON")){
                result.data!!.getStringExtra("COUPON").let {
                    mCoupon = Gson().fromJson(it!!, Coupon::class.java)
                    onConfirm.invoke(mCoupon!!, false)
                    dismiss()
                }
            }
        }
    }

}