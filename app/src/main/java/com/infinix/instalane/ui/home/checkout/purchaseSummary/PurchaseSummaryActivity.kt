package com.infinix.instalane.ui.home.checkout.purchaseSummary

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.infinix.instalane.R
import com.infinix.instalane.data.SingletonProduct
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.data.remote.response.Order
import com.infinix.instalane.data.remote.response.Product
import com.infinix.instalane.data.remote.response.Store
import com.infinix.instalane.databinding.ActivityPurchaseSummaryBinding
import com.infinix.instalane.ui.base.ActivityAppBase
import com.infinix.instalane.ui.home.barcode.ProductAdapter
import com.infinix.instalane.utils.DateUtils
import com.infinix.instalane.utils.showErrorMessage
import com.infinix.instalane.utils.showMessage
import com.infinix.instalane.utils.visible
import net.glxn.qrgen.android.QRCode
import net.glxn.qrgen.core.image.ImageType
import java.util.Calendar

class PurchaseSummaryActivity : ActivityAppBase() {

    companion object{
        private const val ARG_STORE = "ARG_STORE"
        private const val ARG_ORDER = "ARG_ORDER"

        fun getIntent(context: Context, store: Store, order: Order)=
            Intent(context, PurchaseSummaryActivity::class.java).apply {
                putExtra(ARG_STORE, Gson().toJson(store))
                putExtra(ARG_ORDER, Gson().toJson(order))
            }
    }

    private val viewModel by lazy {
        ViewModelProvider(this)[PurchaseViewModel::class.java].apply {
            productsLiveData.observe(this@PurchaseSummaryActivity, this@PurchaseSummaryActivity::showData)
            addReviewLiveData.observe(this@PurchaseSummaryActivity){
                hideProgressDialog()
                showMessage("Review was sent")
                finish()
            }
            onError.observe(this@PurchaseSummaryActivity) {
                hideProgressDialog()
                showErrorMessage(it)
            }
        }
    }

    private var mStore:Store?=null
    private var mOrder:Order?=null
    private val binding by lazy { ActivityPurchaseSummaryBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setToolbar(getString(R.string.purchase_summary))
        showClose()
        intent.getStringExtra(ARG_STORE)?.let { mStore = Gson().fromJson(it, Store::class.java) }
        intent.getStringExtra(ARG_ORDER)?.let { mOrder = Gson().fromJson(it, Order::class.java) }

        binding.mToolBar.mClose.setOnClickListener {showRateDialog()}

        binding.mTitleStore.text = mStore?.name
        binding.mAddress.text = mStore?.address

        val sDate = DateUtils().convertFromDateToString(Calendar.getInstance().time, DateUtils.FORMAT_ORDER_DATE_COMPLETE)
        binding.mDate.text = sDate.replace("a. m.", "AM").replace("p. m.", "PM")

        viewModel.getProducts()
        completeTotal()
        generateQR()
        AppPreferences.cleanDraft(mStore!!)

        Glide.with(this).load(R.drawable.ic_stripe).into(binding.mCardPhoto)
        binding.mCardBrand.text = "Stripe"
        if (!mOrder?.paymentMethodBrand.isNullOrEmpty()){
            binding.mCardData.visible()
            binding.mCardData.text = "**** **** **** ${mOrder!!.paymentMethodLast4}"

            when(mOrder!!.paymentMethodBrand?.toLowerCase()){
                "visa"-> {
                    binding.mCardBrand.text = mOrder!!.paymentMethodBrand?.capitalize()
                    Glide.with(this).load(R.drawable.sample_card).into(binding.mCardPhoto)
                }

                "mastercard"-> {
                    binding.mCardBrand.text = mOrder!!.paymentMethodBrand?.capitalize()
                    Glide.with(this).load(R.drawable.ic_master_card).into(binding.mCardPhoto)
                }

                "amex"-> {
                    binding.mCardBrand.text = "American Express"
                    Glide.with(this).load(R.drawable.ic_american_express).into(binding.mCardPhoto)
                }

                "discover"-> {
                    binding.mCardBrand.text = mOrder!!.paymentMethodBrand?.capitalize()
                    Glide.with(this).load(R.drawable.ic_discover).into(binding.mCardPhoto)
                }
                else -> {
                    binding.mCardBrand.text = "Stripe"
                    Glide.with(this).load(R.drawable.ic_stripe).into(binding.mCardPhoto)
                    binding.mCardData.text = "**** **** **** ${mOrder!!.paymentMethodLast4}"
                }
            }
        }
    }

    private fun generateQR(){
        val bitmapQR = QRCode.from(mOrder!!.id!!).to(ImageType.PNG).withSize(300, 300).bitmap()
        Glide.with(this).load(bitmapQR).into(binding.mQR)
    }

    private fun completeTotal() {
        binding.mSubtotal.text = "$${String.format("%.2f", mOrder!!.subTotal)}".replace(",", ".")
        binding.mDiscount.text = "-$${String.format("%.2f", mOrder!!.discount)}".replace(",", ".")
        binding.mFee.text = "$${String.format("%.2f", mOrder!!.fee)}".replace(",", ".")
        binding.mTaxes.text = "$${String.format("%.2f", mOrder!!.taxes)}".replace(",", ".")
        binding.mTotal.text = "$${String.format("%.2f", mOrder!!.amount)}".replace(",", ".")
    }

    private fun showData(list : List<Product> ){
        binding.mList.adapter = ProductAdapter(ArrayList(list), mStore)
    }

    private fun showRateDialog(){
        val dialog = RateDialogFragment()
        dialog.onConfirm = { stars, comment ->
            showProgressDialog()
            viewModel.addReview(mStore!!, stars, comment)
        }
        dialog.onClose = {finish()}
        dialog.show(supportFragmentManager, "")
    }

    override fun finish() {
        SingletonProduct.instance.clearList()
        super.finish()
    }
}