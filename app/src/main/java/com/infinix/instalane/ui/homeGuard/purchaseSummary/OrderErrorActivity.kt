package com.infinix.instalane.ui.homeGuard.purchaseSummary

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Order
import com.infinix.instalane.databinding.ActivityPaymentErrorBinding
import com.infinix.instalane.utils.DateUtils
import java.util.*

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

        binding.mGuard.text = mOrder!!.guard?.fullname
        binding.mAddress.text = mOrder!!.store?.name

        val date = DateUtils().convertFromStringToDate(mOrder!!.createdAt!!, DateUtils.DATE_TIME_FORMAT_DEFAULT)
        if (date != null) {
            val timeZone: String = Calendar.getInstance().timeZone.id
            val local = Date(date.time + TimeZone.getTimeZone(timeZone).getOffset(date.time))
            val sDate =  DateUtils().convertFromDateToString(local, DateUtils.FORMAT_ORDER_DATE)
            val sTime = DateUtils().convertFromDateToString(local, DateUtils.FORMAT_HOUR)
            binding.mDate.text = sDate
            binding.mTime.text = sTime.replace("a. m.", "AM").replace("p. m.", "PM")
        }else{
            val sDate = DateUtils().convertDate(mOrder!!.createdAt!!, DateUtils.DATE_TIME_FORMAT_DEFAULT, DateUtils.FORMAT_ORDER_DATE)
            val sTime = DateUtils().convertDate(mOrder!!.createdAt!!, DateUtils.DATE_TIME_FORMAT_DEFAULT, DateUtils.FORMAT_HOUR)
            binding.mDate.text = sDate
            binding.mTime.text = sTime
        }

        val mediaPlayer = MediaPlayer.create(this, R.raw.error_payment)
        mediaPlayer.start()
    }
}