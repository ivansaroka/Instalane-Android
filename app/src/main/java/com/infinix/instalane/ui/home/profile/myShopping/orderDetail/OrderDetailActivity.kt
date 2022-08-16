package com.infinix.instalane.ui.home.profile.myShopping.orderDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Order
import com.infinix.instalane.data.remote.response.PurchaseHistory
import com.infinix.instalane.databinding.ActivityOrderDetailBinding
import com.infinix.instalane.ui.base.ActivityAppBase
import com.infinix.instalane.ui.home.profile.myShopping.MyShoppingViewModel
import com.infinix.instalane.utils.listenerAfterTextChanged
import com.infinix.instalane.utils.showErrorMessage

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
        setToolbar("Washington Food Plaza")
        binding.mBackCircle.setOnClickListener { finish() }

        intent.getStringExtra(ARG_SHOPPING)!!.let {
            mPurchaseHistory = Gson().fromJson(it, PurchaseHistory::class.java)
            setToolbar(mPurchaseHistory?.store?.name)
        }

        binding.mComment.listenerAfterTextChanged { binding.mConfirm.isEnabled = it.isNotEmpty() && rateSelected!=null }
        binding.mRate1.setOnClickListener { unselectAll(binding.mRate1, 1) }
        binding.mRate2.setOnClickListener { unselectAll(binding.mRate2, 2) }
        binding.mRate3.setOnClickListener { unselectAll(binding.mRate3, 3) }
        binding.mRate4.setOnClickListener { unselectAll(binding.mRate4, 4) }
        binding.mRate5.setOnClickListener { unselectAll(binding.mRate5, 5) }

        viewModel.getOrder(mPurchaseHistory!!)
    }

    private fun showData(order: Order){
        hideProgressDialog()
        if (!order.items.isNullOrEmpty())
            binding.mList.adapter = OrderAdapter(order.items!!, store = mPurchaseHistory?.store)
    }

    private fun unselectAll(selectItem : ImageView, rate:Int){
        binding.mRate1.setBackgroundResource(R.drawable.circle_gray)
        binding.mRate2.setBackgroundResource(R.drawable.circle_gray)
        binding.mRate3.setBackgroundResource(R.drawable.circle_gray)
        binding.mRate4.setBackgroundResource(R.drawable.circle_gray)
        binding.mRate5.setBackgroundResource(R.drawable.circle_gray)

        selectItem.setBackgroundResource(R.drawable.circle_orange)
        rateSelected = rate
        binding.mConfirm.isEnabled =  binding.mComment.text.toString().isNotEmpty()
    }
}