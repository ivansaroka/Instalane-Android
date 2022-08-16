package com.infinix.instalane.ui.home.checkout.paymentResult

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.infinix.instalane.databinding.ActivityPaymentErrorBinding
import com.infinix.instalane.databinding.ActivityPaymentSuccessfulBinding

class PaymentErrorActivity : AppCompatActivity() {

    private val binding by lazy { ActivityPaymentErrorBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.mClose.setOnClickListener { finish() }
    }
}