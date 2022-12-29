package com.infinix.instalane.ui.home.store

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Coupon
import com.infinix.instalane.data.remote.response.Review
import com.infinix.instalane.data.remote.response.Store
import com.infinix.instalane.databinding.ActivitySeeAllBinding
import com.infinix.instalane.ui.base.ActivityAppBase
import com.infinix.instalane.ui.home.StoreAdapter

class SeeAllActivity : ActivityAppBase() {

    private val viewModel by lazy {
        ViewModelProvider(this)[StoreDialogViewModel::class.java].apply {
            couponLiveData.observe(this@SeeAllActivity, this@SeeAllActivity::showDiscounts)
            reviewLiveData.observe(this@SeeAllActivity, this@SeeAllActivity::showReviews)
            nearLiveData.observe(this@SeeAllActivity, this@SeeAllActivity::showNearStores)
            recommendationLiveData.observe(this@SeeAllActivity, this@SeeAllActivity::showRecommendedStores)
            onError.observe(this@SeeAllActivity) {
                hideProgressDialog()
            }
        }
    }

    companion object {
        const val ARG_TITLE = "ARG_TITLE"
        const val ARG_STORE = "ARG_STORE"

        fun getIntent(context: Context, title:String) =
            Intent(context, SeeAllActivity::class.java).apply {
            putExtra(ARG_TITLE, title)
        }

        fun getIntent(context: Context, title:String, store:Store) =
            Intent(context, SeeAllActivity::class.java).apply {
                putExtra(ARG_TITLE, title)
                putExtra(ARG_STORE, Gson().toJson(store))
            }
    }

    private val binding by lazy { ActivitySeeAllBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setToolbar("")
        intent.getStringExtra(ARG_TITLE).apply {
            setToolbar(this)
            when(this){
                getString(R.string.best_offers) -> getDiscounts()
                getString(R.string.reviews) -> getReviews()
                getString(R.string.stores_visited) -> viewModel.getNearStores()
                getString(R.string.recommendations_near_you) -> viewModel.getRecommendedStores()
            }
        }
    }

    private fun getDiscounts(){
        intent.getStringExtra(ARG_STORE).apply {
            val store = Gson().fromJson(this, Store::class.java)
            viewModel.getDiscount(store)
        }
    }

    private fun getReviews() {
        intent.getStringExtra(ARG_STORE).apply {
            val store = Gson().fromJson(this, Store::class.java)
            viewModel.getReview(store)
        }
    }

    private fun showDiscounts(list:List<Coupon>){
        val adapterOffer = OfferAdapter(list)
        adapterOffer.onUse = { coupon ->
            val dialog = CouponDialogFragment(coupon)
            dialog.show(supportFragmentManager, "")
        }
        binding.mList.adapter = adapterOffer
    }

    private fun showReviews(list:List<Review>) {
        binding.mList.adapter = ReviewAdapter(ArrayList(list))
    }

    private fun showNearStores(list:List<Store>){
        binding.mList.setPadding(10,10,10,10)
        binding.mList.layoutManager = GridLayoutManager(this, 2)
        val adapter = StoreAdapter(list, true)
        adapter.onItemSelected = {
            val dialog = StoreDialogFragment(it)
            dialog.show(supportFragmentManager, "")
        }
        binding.mList.adapter = adapter
    }

    private fun showRecommendedStores(list:List<Store>){
        binding.mList.setPadding(10,10,10,10)
        binding.mList.layoutManager = GridLayoutManager(this, 2)
        val adapter = StoreAdapter(list, true)
        adapter.onItemSelected = {
            val dialog = StoreDialogFragment(it)
            dialog.show(supportFragmentManager, "")
        }
        binding.mList.adapter = adapter
    }

}