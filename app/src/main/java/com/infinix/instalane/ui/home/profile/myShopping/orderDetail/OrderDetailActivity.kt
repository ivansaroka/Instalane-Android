package com.infinix.instalane.ui.home.profile.myShopping.orderDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.infinix.instalane.R
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.data.remote.response.Order
import com.infinix.instalane.data.remote.response.PurchaseHistory
import com.infinix.instalane.databinding.ActivityOrderDetailBinding
import com.infinix.instalane.ui.base.ActivityAppBase
import com.infinix.instalane.ui.home.profile.myShopping.MyShoppingViewModel
import com.infinix.instalane.utils.*
import net.glxn.qrgen.android.QRCode
import net.glxn.qrgen.core.image.ImageType

class OrderDetailActivity : ActivityAppBase() {

    companion object{
        const val ARG_SHOPPING = "ARG_SHOPPING"

        fun getIntent(context: Context, purchaseHistory: PurchaseHistory) =
            Intent(context, OrderDetailActivity::class.java)
                .putExtra(ARG_SHOPPING, Gson().toJson(purchaseHistory))
    }

    private val viewModel by lazy {
        ViewModelProvider(this)[MyShoppingViewModel::class.java].apply {
            orderLiveData.observe(this@OrderDetailActivity, this@OrderDetailActivity::showData)
            addReviewLiveData.observe(this@OrderDetailActivity){
                hideProgressDialog()
                showMessage("Review was sent")
                binding.mContResult.gone()
            }
            onError.observe(this@OrderDetailActivity) {
                hideProgressDialog()
                showErrorMessage(it)
            }
        }
    }

    private val binding by lazy { ActivityOrderDetailBinding.inflate(layoutInflater) }
    var mPurchaseHistory : PurchaseHistory?=null
    var rateSelected :Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setToolbar("")
        binding.mBackCircle.setOnClickListener { finish() }

        intent.getStringExtra(ARG_SHOPPING)!!.let {
            mPurchaseHistory = Gson().fromJson(it, PurchaseHistory::class.java)
            setToolbar(mPurchaseHistory?.store?.name)
            binding.mAddress.text = mPurchaseHistory?.store?.address
            Glide.with(this).load(mPurchaseHistory?.store?.picture).into(binding.mPhoto)
        }

        binding.mComment.listenerAfterTextChanged { binding.mConfirm.isEnabled = it.isNotEmpty() && rateSelected!=null }
        binding.mRate1.setOnClickListener { unselectAll(binding.mRate1, 1) }
        binding.mRate2.setOnClickListener { unselectAll(binding.mRate2, 2) }
        binding.mRate3.setOnClickListener { unselectAll(binding.mRate3, 3) }
        binding.mRate4.setOnClickListener { unselectAll(binding.mRate4, 4) }
        binding.mRate5.setOnClickListener { unselectAll(binding.mRate5, 5) }

        binding.mConfirm.setOnClickListener {
            showProgressDialog()
            viewModel.addReview(mPurchaseHistory?.store!!, rateSelected!!, binding.mComment.text.toString())
        }

        showProgressDialog()
        viewModel.getOrder(mPurchaseHistory!!)
    }

    private fun showData(order: Order){
        hideProgressDialog()
        completeTotal(order)
        generateQR(order)
        checkRating(order)
        if (!order.items.isNullOrEmpty())
            binding.mList.adapter = OrderAdapter(order.items!!, store = mPurchaseHistory?.store)
    }

    private fun checkRating(order: Order){
        binding.mContResult.visible()
        val user = AppPreferences.getUser()!!
        if (!order.store?.ratingsList.isNullOrEmpty()){
            val result = order.store!!.ratingsList!!.filter { it.user == user.id }
            if (result.isNotEmpty())
                binding.mContResult.gone()
        }
    }

    private fun generateQR(order: Order){
        val bitmapQR = QRCode.from(order.id!!).to(ImageType.PNG).withSize(300, 300).bitmap()
        Glide.with(this).load(bitmapQR).into(binding.mQR)
    }

    private fun completeTotal(order: Order) {
        binding.mSubtotal.text = "$${String.format("%.2f", order!!.subTotal)}"
        binding.mDiscount.text = "-$${String.format("%.2f", order!!.discount)}"
        binding.mFee.text = "$${String.format("%.2f", order!!.fee)}"
        binding.mTaxes.text = "$${String.format("%.2f", order!!.taxes)}"
        binding.mTotal.text = "$${String.format("%.2f", order!!.amount)}"
    }

    private fun unselectAll(selectItem : ImageView, rate:Int){
        unselectState()
        rateSelected = rate
        when(rate){
            1 ->{
                binding.mRate1.setBackgroundResource(R.drawable.circle_orange)
            }
            2 ->{
                binding.mRate1.setBackgroundResource(R.drawable.circle_orange)
                binding.mRate2.setBackgroundResource(R.drawable.circle_orange)
            }
            3 ->{
                binding.mRate1.setBackgroundResource(R.drawable.circle_orange)
                binding.mRate2.setBackgroundResource(R.drawable.circle_orange)
                binding.mRate3.setBackgroundResource(R.drawable.circle_orange)
            }
            4 ->{
                binding.mRate1.setBackgroundResource(R.drawable.circle_orange)
                binding.mRate2.setBackgroundResource(R.drawable.circle_orange)
                binding.mRate3.setBackgroundResource(R.drawable.circle_orange)
                binding.mRate4.setBackgroundResource(R.drawable.circle_orange)
            }
            5 ->{
                binding.mRate1.setBackgroundResource(R.drawable.circle_orange)
                binding.mRate2.setBackgroundResource(R.drawable.circle_orange)
                binding.mRate3.setBackgroundResource(R.drawable.circle_orange)
                binding.mRate4.setBackgroundResource(R.drawable.circle_orange)
                binding.mRate5.setBackgroundResource(R.drawable.circle_orange)
            }
        }

        rateSelected = rate
        binding.mConfirm.isEnabled =  binding.mComment.text.toString().isNotEmpty()
    }

    private fun unselectState() {
        binding.mRate1.setBackgroundResource(R.drawable.circle_gray)
        binding.mRate2.setBackgroundResource(R.drawable.circle_gray)
        binding.mRate3.setBackgroundResource(R.drawable.circle_gray)
        binding.mRate4.setBackgroundResource(R.drawable.circle_gray)
        binding.mRate5.setBackgroundResource(R.drawable.circle_gray)
    }
}