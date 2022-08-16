package com.infinix.instalane.ui.home.checkout

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.infinix.instalane.R
import com.infinix.instalane.data.SingletonProduct
import com.infinix.instalane.data.remote.response.Coupon
import com.infinix.instalane.data.remote.response.Order
import com.infinix.instalane.data.remote.response.Product
import com.infinix.instalane.data.remote.response.Store
import com.infinix.instalane.databinding.ActivityCheckoutBinding
import com.infinix.instalane.ui.base.ActivityAppBase
import com.infinix.instalane.ui.home.barcode.ProductAdapter
import com.infinix.instalane.ui.home.checkout.changePayment.ChangePaymentDialogFragment
import com.infinix.instalane.ui.home.checkout.couponLector.ScannedCouponDialogFragment
import com.infinix.instalane.ui.home.checkout.paymentResult.PaymentSuccessfulActivity

class CheckoutActivity : ActivityAppBase() {

    companion object{
        private const val ARG_STORE = "ARG_STORE"

        fun getIntent(context: Context, store: Store)=
            Intent(context, CheckoutActivity::class.java).apply {
                putExtra(ARG_STORE, Gson().toJson(store)) }
    }

    private val viewModel by lazy {
        ViewModelProvider(this)[CheckoutViewModel::class.java].apply {
            productsLiveData.observe(this@CheckoutActivity, this@CheckoutActivity::showData)
            orderLiveData.observe(this@CheckoutActivity, this@CheckoutActivity::paymentSuccessful)
            onError.observe(this@CheckoutActivity) { hideProgressDialog() }
        }
    }

    private var mStore:Store?=null
    private var mProduct:Product?=null
    private val binding by lazy { ActivityCheckoutBinding.inflate(layoutInflater) }

    private var subtotal = 0f
    private var discount = 0f
    private var fee = 0f
    private var taxes = 0f
    private var total = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setToolbar(getString(R.string.checkout))
        intent.getStringExtra(ARG_STORE)?.let { mStore = Gson().fromJson(it, Store::class.java) }

        binding.mListCoupons.adapter = CouponAppliedAdapter(ArrayList())
        (binding.mListCoupons.adapter as CouponAppliedAdapter).onRemoveAll = {
            binding.mTitleCouponApplied.visibility = View.GONE
            binding.mListCoupons.visibility = View.GONE
            completeTotal()
        }
        binding.mCoupon.setOnClickListener {
            mProduct = null
            showCouponDialog()
        }
        binding.mChangeCard.setOnClickListener { showChangePaymentDialog() }
        binding.mConfirm.setOnClickListener { createOrder() }

        viewModel.getProducts()
        completeTotal()
    }

    private fun completeTotal() {
        subtotal = SingletonProduct.instance.getSubtotal()
        discount = (binding.mListCoupons.adapter as CouponAppliedAdapter).getTotalDiscount(subtotal)
        fee = 0f
        taxes = 0f

        val discountBasket = if (binding.mList.adapter!=null) (binding.mList.adapter as ProductAdapter).calculateDiscount() else 0f
        discount += discountBasket
        total = subtotal - discount + fee + taxes

        binding.mSubtotal.text = "$${String.format("%.2f", subtotal)}"
        if (discount == 0f)
            binding.mDiscount.text = "$${String.format("%.2f", discount)}"
        else
            binding.mDiscount.text = "-$${String.format("%.2f", discount)}"
        binding.mFee.text = "$${String.format("%.2f", fee)}"
        binding.mTaxes.text = "$${String.format("%.2f", taxes)}"
        binding.mTotal.text = "$${String.format("%.2f", total)}"
    }

    private fun createOrder() {
        showProgressDialog()
        viewModel.createOrder(subtotal, discount, fee, taxes, total, mStore!!.id!!)
    }

    private fun paymentSuccessful(order: Order) {
        hideProgressDialog()
        startActivity(PaymentSuccessfulActivity.getIntent(this, mStore!!,order))
        val intent = Intent()
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun showCouponDialog(){
        val dialog = AddCouponDialogFragment(mStore!!)
        dialog.onConfirm = {coupon, mustShowConfirmation ->
            if (mustShowConfirmation)
                showScannedCoupon(coupon)
            else
                addCoupon(coupon)
        }
        dialog.show(supportFragmentManager, "")
    }

    private fun showScannedCoupon(coupon: Coupon){
        hideProgressDialog()
        val dialog = ScannedCouponDialogFragment(coupon)
        dialog.onAdd = { addCoupon(coupon) }
        dialog.onCancel = {  }
        dialog.show(supportFragmentManager, "")
    }

    private fun addCoupon(coupon: Coupon) {

        if (mProduct==null){
            binding.mTitleCouponApplied.visibility = View.VISIBLE
            binding.mListCoupons.visibility = View.VISIBLE
            (binding.mListCoupons.adapter as CouponAppliedAdapter).addCoupon(coupon, this)
        } else{
            (binding.mList.adapter as ProductAdapter).addCoupon(coupon, mProduct!!)
            mProduct = null
        }
        completeTotal()
    }

    private fun showChangePaymentDialog(){
        val dialog = ChangePaymentDialogFragment()
        dialog.onSelectedMethod = {}
        dialog.show(supportFragmentManager, "")
    }

    private fun showData(list : List<Product>){
        val adapter = ProductAdapter(ArrayList(list), mStore, true)
        adapter.onAddCoupon = {
            mProduct = it
            showCouponDialog()
        }
        adapter.onRemoveCoupon = {
            completeTotal()
        }
        binding.mList.adapter = adapter
    }

}