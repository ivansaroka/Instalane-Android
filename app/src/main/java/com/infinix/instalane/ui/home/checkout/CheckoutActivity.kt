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
import com.infinix.instalane.data.remote.response.*
import com.infinix.instalane.databinding.ActivityCheckoutBinding
import com.infinix.instalane.ui.base.ActivityAppBase
import com.infinix.instalane.ui.home.barcode.ProductAdapter
import com.infinix.instalane.ui.home.checkout.changePayment.ChangePaymentDialogFragment
import com.infinix.instalane.ui.home.checkout.couponLector.ScannedCouponDialogFragment
import com.infinix.instalane.ui.home.checkout.paymentResult.PaymentSuccessfulActivity
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult

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
            orderLiveData.observe(this@CheckoutActivity){ mNewOrder = it }
            paymentIntentLiveData.observe(this@CheckoutActivity, this@CheckoutActivity::onPaymentIntentSuccess)
            onError.observe(this@CheckoutActivity) { hideProgressDialog() }
        }
    }

    private var mStore:Store?=null
    private var mProduct:Product?=null
    private val binding by lazy { ActivityCheckoutBinding.inflate(layoutInflater) }

    private var subtotal = 0f
    private var discount = 0f
    private var mStripeFee = 0f
    private var fee = 0f
    private var taxes = 0f
    private var total = 0f
    private var couponList = ArrayList<String>()

    lateinit var paymentSheet: PaymentSheet
    lateinit var customerConfig: PaymentSheet.CustomerConfiguration
    lateinit var paymentIntentClientSecret: String
    var mNewOrder: Order?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setToolbar(getString(R.string.checkout))
        intent.getStringExtra(ARG_STORE)?.let { mStore = Gson().fromJson(it, Store::class.java) }
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)

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
        mStripeFee = (subtotal * 2.9f) / 100
        fee = 1.5f
        taxes = if (!mStore?.regionTax.isNullOrEmpty()) mStore?.regionTax!!.replace(",", ".").toFloat() else 0f

        val discountBasket = if (binding.mList.adapter!=null) (binding.mList.adapter as ProductAdapter).calculateDiscount() else 0f
        discount += discountBasket
        total = subtotal - discount + mStripeFee + fee + taxes
        total = String.format("%.2f", total).replace(",", ".").toFloat()

        binding.mSubtotal.text = "$${String.format("%.2f", subtotal)}"
        if (discount == 0f)
            binding.mDiscount.text = "$${String.format("%.2f", discount)}"
        else
            binding.mDiscount.text = "-$${String.format("%.2f", discount)}"

        binding.mStripeFee.text = "$${String.format("%.2f", mStripeFee)}"
        binding.mFee.text = "$${String.format("%.2f", fee)}"
        binding.mTaxes.text = "$${String.format("%.2f", taxes)}"
        binding.mTotal.text = "$${String.format("%.2f", total)}"


        couponList.clear()
        val generalCouponList = (binding.mListCoupons.adapter as CouponAppliedAdapter).getCouponIdList()
        val productCouponList = if (binding.mList.adapter!=null) (binding.mList.adapter as ProductAdapter).getCouponIdList() else ArrayList()
        couponList.addAll(generalCouponList)
        couponList.addAll(productCouponList)
    }

    private fun createOrder() {
        showProgressDialog()
        viewModel.createOrder(subtotal, discount, fee, taxes, total, mStore!!.id!!, couponList)
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

    private fun onPaymentIntentSuccess(paymentIntentResponse: PaymentIntentResponse){
        hideProgressDialog()
        paymentIntentClientSecret = paymentIntentResponse.paymentIntent!!
        customerConfig = PaymentSheet.CustomerConfiguration(
            paymentIntentResponse.customer!!,
            paymentIntentResponse.ephemeralKey!!
        )
        val publishableKey = paymentIntentResponse.publishableKey
        PaymentConfiguration.init(this, publishableKey!!)

        presentPaymentSheet()
    }

    private fun presentPaymentSheet() {
        paymentSheet.presentWithPaymentIntent(
            paymentIntentClientSecret,
            PaymentSheet.Configuration(
                merchantDisplayName = "Order",
                customer = customerConfig,
                // Set `allowsDelayedPaymentMethods` to true if your business
                // can handle payment methods that complete payment after a delay, like SEPA Debit and Sofort.
                allowsDelayedPaymentMethods = false
            )
        )
    }

    private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult){
        when(paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                print("Canceled")
            }
            is PaymentSheetResult.Failed -> {
                print("Error: ${paymentSheetResult.error}")
            }
            is PaymentSheetResult.Completed -> {
                if (mNewOrder!=null)
                    paymentSuccessful(mNewOrder!!)
                //changeStatus(Order.STATUS_COMPLETED, mOrder)
            }
        }
    }

}