package com.infinix.instalane.ui.home.profile.myShopping

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.PurchaseHistory
import com.infinix.instalane.databinding.ActivityMyShoppingBinding
import com.infinix.instalane.ui.base.ActivityAppBase
import com.infinix.instalane.ui.base.EmptyAdapter
import com.infinix.instalane.ui.home.profile.myShopping.orderDetail.OrderDetailActivity
import com.infinix.instalane.utils.showErrorMessage

class MyShoppingActivity : ActivityAppBase() {

    private val binding by lazy { ActivityMyShoppingBinding.inflate(layoutInflater) }

    private val viewModel by lazy {
        ViewModelProvider(this)[MyShoppingViewModel::class.java].apply {
            myPurchaseHistoryLiveData.observe(this@MyShoppingActivity, this@MyShoppingActivity::showData)
            onError.observe(this@MyShoppingActivity) {
                hideProgressDialog()
                showErrorMessage(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setToolbar(getString(R.string.my_shopping))
        showProgressDialog()
        viewModel.getMyShopping()
    }

    private fun showData(list:List<PurchaseHistory>) {
        hideProgressDialog()
        if (list.isEmpty())
            binding.mList.adapter = EmptyAdapter(getString(R.string.empty_shopping))
        else {
            val adapter = MyShoppingAdapter(list)
            adapter.onItemSelected = { startActivity(OrderDetailActivity.getIntent(this, it)) }
            binding.mList.adapter = adapter
        }
    }
}