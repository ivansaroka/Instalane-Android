package com.infinix.instalane.ui.home.profile.paymentMethods

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Card
import com.infinix.instalane.databinding.ActivityPaymentMethodsBinding
import com.infinix.instalane.ui.base.ActivityAppBase
import com.infinix.instalane.ui.home.profile.paymentMethods.addCard.AddNewCardActivity

class PaymentMethodsActivity : ActivityAppBase() {

    private val viewModel by lazy {
        ViewModelProvider(this)[PaymentViewModel::class.java].apply {
            cardLiveData.observe(this@PaymentMethodsActivity, this@PaymentMethodsActivity::showData)
            onError.observe(this@PaymentMethodsActivity) { hideProgressDialog() }
        }
    }

    private val binding by lazy { ActivityPaymentMethodsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setToolbar(getString(R.string.payment_methods))
        binding.mConfirm.setOnClickListener { startActivity(Intent(this, AddNewCardActivity::class.java)) }
        viewModel.getCards()
    }

    private fun showData(list:List<Card>){
        hideProgressDialog()
        binding.mList.adapter = PaymentMethodAdapter(list)
    }
}