package com.infinix.instalane.ui.home.checkout

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
            couponLiveData.observe(this@CheckoutActivity) {
                mCouponList.addAll(it)
                getProducts()
            }
            productsLiveData.observe(this@CheckoutActivity, this@CheckoutActivity::showData)
            orderLiveData.observe(this@CheckoutActivity){ mNewOrder = it }
            updateOrderLiveData.observe(this@CheckoutActivity){
                hideProgressDialog()
                mNewOrder = it
                paymentSuccessful(mNewOrder!!)
            }
            paymentIntentLiveData.observe(this@CheckoutActivity, this@CheckoutActivity::onPaymentIntentSuccess)
            onError.observe(this@CheckoutActivity) { hideProgressDialog() }
        }
    }

    private var mCouponList = ArrayList<Coupon>()
    private var mStore:Store?=null
    private var mProduct:Product?=null
    private val binding by lazy { ActivityCheckoutBinding.inflate(layoutInflater) }

    private var subtotal = 0f
    private var discount = 0f
    //private var mStripeFee = 0f
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

        binding.mCoupon.setOnClickListener {
            mProduct = null
            showCouponDialog()
        }
        binding.mChangeCard.setOnClickListener { showChangePaymentDialog() }
        binding.mConfirm.setOnClickListener { createOrder() }


        viewModel.getCoupons(mStore!!)
        completeTotal()
    }


    private fun completeTotal() {
        subtotal = SingletonProduct.instance.getSubtotal()
        //discount = (binding.mListCoupons.adapter as CouponAppliedAdapter).getTotalDiscount(subtotal)

        fee = 1.99f
        //taxes = if (!mStore?.regionTax.isNullOrEmpty()) mStore?.regionTax!!.replace(",", ".").toFloat() else 0f
        taxes = SingletonProduct.instance.getTotalTaxes()


        val discountBasket = if (binding.mList.adapter!=null) (binding.mList.adapter as ProductAdapter).calculateDiscount() else 0f
        discount = discountBasket

        total = subtotal - discount + fee + taxes
        total = String.format("%.2f", total).replace(",", ".").toFloat()


        binding.mSubtotal.text = "$${String.format("%.2f", subtotal)}".replace(",", ".")
        if (discount == 0f)
            binding.mDiscount.text = "$${String.format("%.2f", discount)}".replace(",", ".")
        else
            binding.mDiscount.text = "-$${String.format("%.2f", discount)}".replace(",", ".")
        binding.mFee.text = "$${String.format("%.2f", fee)}".replace(",", ".")
        binding.mTaxes.text = "$${String.format("%.2f", taxes)}".replace(",", ".")
        binding.mTotal.text = "$${String.format("%.2f", total)}".replace(",", ".")

        couponList.clear()
        val productCouponList = if (binding.mList.adapter!=null) (binding.mList.adapter as ProductAdapter).getCouponIdList() else ArrayList()
        couponList.addAll(productCouponList)
    }

    private fun createOrder() {
        mustAskForBiometric = false
        showProgressDialog()
        viewModel.createOrder(subtotal, discount, fee, taxes, total, mStore!!.id!!, couponList)
    }

    private fun paymentSuccessful(order: Order) {
        hideProgressDialog()
        order.taxes = taxes
        startActivity(PaymentSuccessfulActivity.getIntent(this, mStore!!,order))
        val intent = Intent()
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun showCouponDialog(){
        val dialog = AddCouponDialogFragment(mStore!!)
        dialog.onConfirm = {coupon, mustShowConfirmation ->

            if (mProduct==null && coupon.category==null){
                showErrorAlert("The coupon you want to use is for products only.")
            } else {
                if (mustShowConfirmation)
                    showScannedCoupon(coupon)
                else
                    addCoupon(coupon)
            }
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
            (binding.mList.adapter as ProductAdapter).addCouponToProducts(coupon)
            /*
            binding.mTitleCouponApplied.visibility = View.VISIBLE
            binding.mListCoupons.visibility = View.VISIBLE
            (binding.mListCoupons.adapter as CouponAppliedAdapter).addCoupon(coupon, this)
             */
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

        if (mCouponList.isNotEmpty() && list.isNotEmpty()){

            mCouponList.forEach { coupon ->

                if (coupon.category == null) {
                    list.forEach { product ->
                        if (!product.relatedCoupons.isNullOrEmpty()){
                            product.relatedCoupons!!.forEach {  relatedCoupon ->
                                if (coupon.couponProducts!!.contains(relatedCoupon.id)) {
                                    product.coupon = coupon
                                }
                            }
                        }
                    }
                }
            }
        }

        val adapter = ProductAdapter(ArrayList(list), mStore, true)
        adapter.onAddCoupon = {
            mProduct = it
            showCouponDialog()
        }
        adapter.onRemoveCoupon = {
            completeTotal()
        }
        adapter.onAddLocalCoupon = {
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
                if (mNewOrder!=null){
                    showProgressDialog()
                    viewModel.getOrder(mNewOrder!!.id!!)
                    //paymentSuccessful(mNewOrder!!)
                }
                //changeStatus(Order.STATUS_COMPLETED, mOrder)
            }
        }
    }

}