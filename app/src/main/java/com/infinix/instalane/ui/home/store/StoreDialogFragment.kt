package com.infinix.instalane.ui.home.store

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Coupon
import com.infinix.instalane.data.remote.response.Review
import com.infinix.instalane.data.remote.response.Store
import com.infinix.instalane.databinding.FragmentStoreBinding
import com.infinix.instalane.ui.home.barcode.BarCodeActivity
import com.infinix.instalane.utils.MembershipDialog

class StoreDialogFragment (private val store: Store) : BottomSheetDialogFragment() {

    private val viewModel by lazy {
        ViewModelProvider(this)[StoreDialogViewModel::class.java].apply {
            couponLiveData.observe(this@StoreDialogFragment, this@StoreDialogFragment::showData)
            reviewLiveData.observe(this@StoreDialogFragment, this@StoreDialogFragment::showReviews)
            onError.observe(this@StoreDialogFragment) {  }
        }
    }


    private val binding by lazy { FragmentStoreBinding.inflate(layoutInflater) }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener {
            val dialog = it as BottomSheetDialog

            dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val containerLayout: FrameLayout =
                dialog.findViewById<FrameLayout>(com.google.android.material.R.id.container) as FrameLayout

            val buttonConfirm = binding.confirm
            val parent = buttonConfirm.parent as ViewGroup
            parent.removeView(buttonConfirm)

            val buttonAddMembership = binding.mAddMembership
            parent.removeView(buttonAddMembership)

            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM)
            params.setMargins(30,30,30,30)
            buttonConfirm.layoutParams = params
            buttonAddMembership.layoutParams = params

            containerLayout.addView(buttonConfirm, params)
            containerLayout.addView(buttonAddMembership, params)

        }
        return bottomSheetDialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.mPhoto.clipToOutline = true
        binding.confirm.setOnClickListener {
            startActivity(BarCodeActivity.getIntent(requireContext(), store))
            dismiss()
        }
        binding.mAddMembership.setOnClickListener {
            val dialog = MembershipDialog()
            dialog.show(childFragmentManager, null)
        }
        binding.mDistance.text ="${String.format("%.0f", store.distance)} miles"
        binding.mContCheckReview.setOnClickListener { startActivity(ReviewActivity.getIntent(requireContext(), store))}
        binding.mContBestOffers.visibility = View.GONE
        viewModel.getDiscount(store)
        //viewModel.getReview(store)
        completeInfo()
    }

    private fun completeInfo(){
        binding.mPhoto.clipToOutline = true
        Glide.with(this).load(store.picture).into(binding.mPhoto)
        binding.mTitle.text = store.name
        binding.mAddress.text = store.address
        binding.mRate.text = store.calculateRate().toString()

        binding.confirm.visibility = View.VISIBLE
        binding.mAddMembership.visibility = View.GONE
        if (store.region?.company?.membershipRequired == true && store.canOpen == false){
            binding.confirm.visibility = View.GONE
            binding.mAddMembership.visibility = View.VISIBLE
        }
    }

    private fun showData(list:List<Coupon>) {
        if (list.isNullOrEmpty()){
            binding.mContBestOffers.visibility = View.GONE
            return
        }

        binding.mContBestOffers.visibility = View.VISIBLE
        binding.mSeeAllOffers.visibility = View.INVISIBLE
        val adapterOffer = if (list.size > 10) {
            binding.mSeeAllOffers.visibility = View.VISIBLE
            OfferAdapter(list.subList(0, 10))
        } else
            OfferAdapter(list)
        binding.mSeeAllOffers.visibility = View.VISIBLE

        adapterOffer.onUse = { coupon ->
            val dialog = CouponDialogFragment(coupon)
            dialog.onUse = { }
            dialog.show(childFragmentManager, "")
        }
        binding.mList.adapter = adapterOffer
        binding.mSeeAllOffers.setOnClickListener { startActivity(SeeAllActivity.getIntent(requireContext(), getString(R.string.best_offers), store)) }
    }

    private fun showReviews(list:List<Review>) {
        binding.mSeeAllReviews.visibility = View.INVISIBLE
        if (list.size > 2) {
            binding.mListReviews.adapter = ReviewAdapter(list.subList(0,2))
            binding.mSeeAllReviews.visibility = View.VISIBLE
        } else
            binding.mListReviews.adapter = ReviewAdapter(list)

        binding.mTotalReviews.text = String.format(getString(R.string.total_reviews), list.size)


        binding.mSeeAllReviews.setOnClickListener { startActivity(SeeAllActivity.getIntent(requireContext(), getString(R.string.reviews), store)) }
    }
}