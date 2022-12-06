package com.infinix.instalane.ui.home.profile.faqs

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Faqs
import com.infinix.instalane.databinding.ActivityFaqsBinding
import com.infinix.instalane.ui.base.ActivityAppBase

class FaqsActivity : ActivityAppBase() {

    private val viewModel by lazy {
        ViewModelProvider(this)[FaqsViewModel::class.java].apply {
            faqsLiveData.observe(this@FaqsActivity, this@FaqsActivity::showData)
            onError.observe(this@FaqsActivity) {
                hideProgressDialog()
                showErrorApi(it) }
        }
    }

    private val binding by lazy { ActivityFaqsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setToolbar(getString(R.string.faqs))

        showProgressDialog()
        viewModel.getFaqs()
    }

    private fun showData(list: List<Faqs>){
        hideProgressDialog()
        binding.mList.adapter = FaqAdapter(list)
    }
}