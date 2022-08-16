package com.infinix.instalane.ui.homeGuard.purchaseSummary

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.infinix.instalane.data.remote.response.Order
import com.infinix.instalane.databinding.ActivityPaymentErrorBinding
import com.infinix.instalane.databinding.ActivityPaymentSuccessfulBinding
import com.infinix.instalane.ui.home.checkout.paymentResult.PaymentSuccessfulActivity
import com.infinix.instalane.utils.DateUtils

class OrderErrorActivity : AppCompatActivity() {

    companion object{
        private const val ARG_ORDER = "ARG_ORDER"

        fun getIntent(context: Context, order : Order) =
            Intent(context, OrderErrorActivity::class.java)
                .apply { putExtra(ARG_ORDER, Gson().toJson(order)) }
    }

    private val binding by lazy { ActivityPaymentErrorBinding.inflate(layoutInflater) }
    private var mOrder:Order?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        intent.getStringExtra(ARG_ORDER)?.let { mOrder = Gson().fromJson(it, Order::class.java) }

        binding.mClose.setOnClickListener { finish() }

        binding.mAddress.text = mOrder!!.store?.name
        val sDate = DateUtils().convertDate(mOrder!!.updatedAt!!, DateUtils.DATE_TIME_FORMAT_DEFAULT, DateUtils.FORMAT_ORDER_DATE)
        val sTime = DateUtils().convertDate(mOrder!!.updatedAt!!, DateUtils.DATE_TIME_FORMAT_DEFAULT, DateUtils.FORMAT_HOUR)
        binding.mDate.text = sDate
        binding.mTime.text = sTime
    }
}