package com.infinix.instalane.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
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


class MainActivity : ActivityAppBase(), OnMapReadyCallback {

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

    private var locationRequest: LocationRequest?=null
    private lateinit var locationCallback: LocationCallback
    private var locationFound = false
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.mMap.setOnClickListener { startActivity(Intent(this, MapsActivity::class.java)) }
        binding.mMap.clipToOutline = true

        binding.mContProfile.setOnClickListener { startActivity(Intent(this, UserProfileActivity::class.java)) }
        binding.mNotification.setOnClickListener { startActivity(Intent(this, NotificationActivity::class.java))  }

        viewModel.sendDeviceToken()

        if (AppPreferences.mustShowTutorial()){
            val dialog = ShowTutorialDialog()
            dialog.show(supportFragmentManager, null)
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        googleMap.uiSettings.isMyLocationButtonEnabled = false
        googleMap.uiSettings.isCompassEnabled = false

        mMap = googleMap
        getCurrentLocation()
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
                    mMap.isMyLocationEnabled = true
                    getLocationUpdates()
                    if(locationRequest!=null) {
                        fusedLocationClient.requestLocationUpdates(
                            locationRequest!!,
                            locationCallback,
                            Looper.getMainLooper()
                        )
                    }
                    Handler(Looper.myLooper()!!).postDelayed({
                        fusedLocationClient.lastLocation
                            .addOnSuccessListener { loc->
                                if (!locationFound) {
                                    locationFound = true
                                    Log.i("GETDATA", "2")
                                    getData(loc)
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(loc.latitude, loc.longitude), 12f))
                                }
                            }
                    }, 2000)

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

    private fun getData(loc:Location?){
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
        } else
            showErrorAlert("Check if your GPS is turned on")
    }

    private fun getLocationUpdates() {
        locationRequest = LocationRequest()
        locationRequest!!.interval = 50000
        locationRequest!!.fastestInterval = 50000
        locationRequest!!.smallestDisplacement = 170f // 170 m = 0.1 mile
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY //set according to your app function
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (!locationFound) {
                    locationFound = true
                    Log.i("GETDATA", "1")
                    if (locationResult.locations.isNotEmpty()){

                        SingletonLocation.instance.myLocation = LatLng(locationResult.lastLocation.latitude, locationResult.lastLocation.longitude)

                        getData(locationResult.lastLocation)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(SingletonLocation.instance.myLocation!!, 15f))
                    }

                }
            }
        }
    }
}