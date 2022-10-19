package com.infinix.instalane.ui.home.checkout.paymentResult

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import com.infinix.instalane.R
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.data.remote.response.Order
import com.infinix.instalane.data.remote.response.Store
import com.infinix.instalane.databinding.ActivityPaymentSuccessfulBinding
import com.infinix.instalane.ui.base.ActivityAppBase
import com.infinix.instalane.ui.home.checkout.purchaseSummary.PurchaseSummaryActivity


class PaymentSuccessfulActivity : ActivityAppBase() {

    companion object{
        private const val ARG_STORE = "ARG_STORE"
        private const val ARG_ORDER = "ARG_ORDER"

        fun getIntent(context: Context, store: Store, order:Order)=
            Intent(context, PaymentSuccessfulActivity::class.java).apply {
                putExtra(ARG_STORE, Gson().toJson(store))
                putExtra(ARG_ORDER, Gson().toJson(order))
            }
    }

    private var mStore:Store?=null
    private var mOrder:Order?=null
    private val binding by lazy { ActivityPaymentSuccessfulBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        intent.getStringExtra(ARG_STORE)?.let { mStore = Gson().fromJson(it, Store::class.java) }
        intent.getStringExtra(ARG_ORDER)?.let { mOrder = Gson().fromJson(it, Order::class.java) }

        binding.mClose.setOnClickListener {
            if (AppPreferences.getUser()?.isUser()==true)
                startActivity(PurchaseSummaryActivity.getIntent(this, mStore!!, mOrder!!))
            finish()
        }

        if (AppPreferences.getUser()?.isUser()==true)
            binding.mTitle.text = getString(R.string.your_payment_was_successful)
        else
            binding.mTitle.text = getString(R.string.order_confirmed)

        val mediaPlayer = MediaPlayer.create(this, R.raw.success_payment)
        mediaPlayer.start()

        Handler(Looper.myLooper()!!).postDelayed({
            if (AppPreferences.getUser()?.isUser()==true)
                startActivity(PurchaseSummaryActivity.getIntent(this, mStore!!, mOrder!!))
            finish()
        }, 3000)
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }
}