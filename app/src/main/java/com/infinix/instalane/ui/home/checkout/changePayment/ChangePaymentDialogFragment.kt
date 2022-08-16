package com.infinix.instalane.ui.home.checkout.changePayment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Card
import com.infinix.instalane.databinding.FragmentChangePaymentBinding
import com.infinix.instalane.ui.home.profile.paymentMethods.PaymentMethodAdapter
import com.infinix.instalane.ui.home.profile.paymentMethods.PaymentViewModel


class ChangePaymentDialogFragment : BottomSheetDialogFragment() {

    private val viewModel by lazy {
        ViewModelProvider(this)[PaymentViewModel::class.java].apply {
            cardLiveData.observe(this@ChangePaymentDialogFragment, this@ChangePaymentDialogFragment::showData)
            onError.observe(this@ChangePaymentDialogFragment) {  }
        }
    }

    private val binding by lazy { FragmentChangePaymentBinding.inflate(layoutInflater) }

    lateinit var onSelectedMethod: (Card)->Unit

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

        viewModel.getCards()

        binding.mChooseMethod.setOnClickListener {
            onSelectedMethod((binding.mList.adapter as PaymentMethodAdapter).getSelectedCard())
            dismiss()
        }
        binding.mClose.setOnClickListener { dismiss() }
    }

    private fun showData(list:List<Card>){
        //hideProgressDialog()
        binding.mList.adapter = PaymentMethodAdapter(list)
    }
}