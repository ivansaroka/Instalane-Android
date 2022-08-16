package com.infinix.instalane.ui.home.barcode

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Product
import com.infinix.instalane.data.remote.response.Store
import com.infinix.instalane.databinding.FragmentScannedProductBinding

class ScannedProductDialogFragment (val product: Product, val store:Store) : BottomSheetDialogFragment() {

    private val binding by lazy { FragmentScannedProductBinding.inflate(layoutInflater) }

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

        Glide.with(requireContext()).load(product.picture).into( binding.mPhoto)
        binding.mName.text = product.name
        binding.mPrice.text = "$${product.getPriceByRegion(store.region)}"

        var quantity = 1
        binding.mQuantityReduce.setOnClickListener {
            quantity--
            if (quantity<1)
                quantity = 1
            binding.mQuantity.text = quantity.toString()
        }

        binding.mQuantityAdd.setOnClickListener {
            quantity++
            binding.mQuantity.text = quantity.toString()
        }


        binding.mAddToBasket.setOnClickListener {
            //product.quantity = quantity
            onAdd()
            dismiss()
        }
        binding.mCancel.setOnClickListener {
            onCancel()
            dismiss()
        }
    }

}