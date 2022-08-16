package com.infinix.instalane.ui.home.barcode

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
import com.infinix.instalane.data.remote.response.Product
import com.infinix.instalane.data.remote.response.Store
import com.infinix.instalane.databinding.FragmentEnterCodeBinding
import com.infinix.instalane.utils.listenerAfterTextChanged
import com.infinix.instalane.utils.showErrorMessage


class EnterCodeDialogFragment (private val mStore:Store) : BottomSheetDialogFragment() {

    private val binding by lazy { FragmentEnterCodeBinding.inflate(layoutInflater) }

    private val viewModel by lazy {
        ViewModelProvider(this)[ProductViewModel::class.java].apply {
            productLiveData.observe(this@EnterCodeDialogFragment, this@EnterCodeDialogFragment::showScannedProduct)
            onError.observe(this@EnterCodeDialogFragment) {
                activity?.showErrorMessage(it)
                binding.mConfirm.visibility = View.VISIBLE
                binding.mProgress.visibility = View.GONE
            }
        }
    }

    lateinit var onConfirm: (Product)->Unit
    lateinit var onCancel: ()->Unit

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

        binding.mCode.listenerAfterTextChanged {
            binding.mConfirm.isEnabled = it.isNotEmpty()
        }

        binding.mConfirm.setOnClickListener {
            binding.mConfirm.visibility = View.GONE
            binding.mProgress.visibility = View.VISIBLE
            viewModel.getProduct(binding.mCode.text.toString(), mStore)
        }

        binding.mClose.setOnClickListener {
            onCancel()
            dismiss()
        }
    }

    private fun showScannedProduct(product: Product){
        onConfirm(product)
        dismiss()
    }

}