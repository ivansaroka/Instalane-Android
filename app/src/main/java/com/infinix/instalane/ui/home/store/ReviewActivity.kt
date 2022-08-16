package com.infinix.instalane.ui.home.store

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Review
import com.infinix.instalane.data.remote.response.Store
import com.infinix.instalane.databinding.ActivityReviewBinding
import com.infinix.instalane.ui.base.ActivityAppBase

class ReviewActivity : ActivityAppBase() {

    private var mStore:Store?=null
    private val binding by lazy { ActivityReviewBinding.inflate(layoutInflater) }

    private val viewModel by lazy {
        ViewModelProvider(this)[StoreDialogViewModel::class.java].apply {
            reviewLiveData.observe(this@ReviewActivity, this@ReviewActivity::showReviews)
            onError.observe(this@ReviewActivity) {  }
        }
    }

    companion object{
        private const val ARG_STORE = "ARG_STORE"

        fun getIntent(context: Context, store: Store)=
            Intent(context, ReviewActivity::class.java).apply {
                putExtra(ARG_STORE, Gson().toJson(store)) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setToolbar(getString(R.string.reviews))

        intent.getStringExtra(ARG_STORE)?.let {
            mStore = Gson().fromJson(it, Store::class.java)
            viewModel.getReview(mStore!!)
        }
    }

    private fun showReviews(list:List<Review>) {
        binding.mListReviews.adapter = ReviewAdapter(list)
    }
}