package com.infinix.instalane.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.infinix.instalane.R
import com.infinix.instalane.data.SingletonLocation
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.data.remote.response.Coupon
import com.infinix.instalane.data.remote.response.Store
import com.infinix.instalane.databinding.ActivityMainBinding
import com.infinix.instalane.ui.base.ActivityAppBase
import com.infinix.instalane.ui.home.map.MapsActivity
import com.infinix.instalane.ui.home.notification.NotificationActivity
import com.infinix.instalane.ui.home.profile.UserProfileActivity
import com.infinix.instalane.ui.home.store.SeeAllActivity
import com.infinix.instalane.ui.home.store.StoreDialogFragment
import com.infinix.instalane.utils.ShowTutorialDialog
import com.tbruyelle.rxpermissions3.RxPermissions
import java.io.IOException
import java.util.*

class MainActivity : ActivityAppBase() {

    private val viewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java].apply {
            nearStoreLiveData.observe(this@MainActivity, this@MainActivity::showNearStores)
            recommendationLiveData.observe(this@MainActivity, this@MainActivity::showRecommendedStores)
            couponLiveData.observe(this@MainActivity, this@MainActivity::showDiscounts)
            onError.observe(this@MainActivity) { hideProgressDialog() }
        }
    }

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(this@MainActivity) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.mMap.setOnClickListener { startActivity(Intent(this, MapsActivity::class.java)) }
        binding.mMap.clipToOutline = true

        binding.mContProfile.setOnClickListener { startActivity(Intent(this, UserProfileActivity::class.java)) }
        binding.mNotification.setOnClickListener { startActivity(Intent(this, NotificationActivity::class.java))  }

        getCurrentLocation()
        viewModel.sendDeviceToken()

        if (AppPreferences.mustShowTutorial()){
            val dialog = ShowTutorialDialog()
            dialog.show(supportFragmentManager, null)
        }
    }

    override fun onResume() {
        super.onResume()
        completeData()
    }

    private fun completeData() {
        val user = AppPreferences.getUser()!!
        binding.mUsername.text = user.fullname
        binding.mProfile.clipToOutline = true
        Glide.with(this).load(user.profilePicture).circleCrop().placeholder(R.drawable.placeholder_user_profile).into(binding.mProfile)
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        val disposePermission = RxPermissions(this)
            .request(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).subscribe { granted: Boolean? ->
                if (granted != null && granted) {
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { loc->
                            if (loc != null) {
                                SingletonLocation.instance.locationSelected = null
                                SingletonLocation.instance.myLocation = LatLng(loc.latitude, loc.longitude)
                                try {
                                    val geocoder = Geocoder(this, Locale.getDefault())
                                    val addresses: List<Address> = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                                    if (!addresses.isNullOrEmpty()) {
                                        val obj = addresses[0]
                                        binding.mLocation.text = if(obj.locality.isEmpty()) obj.locality else obj.subAdminArea

                                        viewModel.getNearStores()
                                        viewModel.getRecommendedStores()
                                        viewModel.getDiscounts()
                                    }
                                }
                                catch (e: IOException){ }
                                catch (e: Exception){ }
                            }
                        }
                }
            }
    }

    private fun showNearStores(list:List<Store>){
        hideProgressDialog()
        val adapter = StoreAdapter(list)
        adapter.onItemSelected = {
            val dialog = StoreDialogFragment(it)
            dialog.show(supportFragmentManager, "")
        }
        binding.mStoreVisitedList.adapter = adapter
        binding.mSeeAllStoreVisited.setOnClickListener { startActivity(SeeAllActivity.getIntent(this, getString(R.string.stores_visited))) }
    }

    private fun showRecommendedStores(list:List<Store>){
        hideProgressDialog()
        val adapter = StoreAdapter(list)
        adapter.onItemSelected = {
            val dialog = StoreDialogFragment(it)
            dialog.show(supportFragmentManager, "")
        }
        binding.mRecommendationList.adapter = adapter
        binding.mSeeAllRecommendation.setOnClickListener { startActivity(SeeAllActivity.getIntent(this, getString(R.string.recommendations_near_you))) }
    }

    private fun showDiscounts(list:List<Coupon>) {
        hideProgressDialog()
        binding.mDiscountList.adapter = DiscountAdapter(list)
    }
}