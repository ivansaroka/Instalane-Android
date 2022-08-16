package com.infinix.instalane.ui.homeGuard

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
import com.infinix.instalane.databinding.ActivityMainGuardBinding
import com.infinix.instalane.ui.base.ActivityAppBase
import com.infinix.instalane.ui.home.MainViewModel
import com.infinix.instalane.ui.homeGuard.profile.GuardProfileActivity
import com.infinix.instalane.ui.homeGuard.scanOrder.ScanOrderActivity
import com.tbruyelle.rxpermissions3.RxPermissions
import java.io.IOException
import java.util.*

class MainGuardActivity : ActivityAppBase() {

    private val viewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java].apply {
            onError.observe(this@MainGuardActivity) { hideProgressDialog() }
        }
    }

    private val binding by lazy { ActivityMainGuardBinding.inflate(layoutInflater) }
    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(this@MainGuardActivity) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.mContProfile.setOnClickListener { startActivity(Intent(this, GuardProfileActivity::class.java)) }
        binding.mStartCheck.setOnClickListener { startActivity(Intent(this, ScanOrderActivity::class.java)) }
        getCurrentLocation()

        viewModel.sendDeviceToken()
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
                                    }
                                }
                                catch (e: IOException){ }
                                catch (e: Exception){ }
                            }
                        }

                }
            }
    }

}