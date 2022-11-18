package com.infinix.instalane.ui.home.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.infinix.instalane.R
import com.infinix.instalane.data.SingletonLocation
import com.infinix.instalane.data.remote.response.Store
import com.infinix.instalane.databinding.ActivityMapsBinding
import com.infinix.instalane.ui.base.ActivityAppBase
import com.infinix.instalane.ui.home.store.StoreDialogFragment
import com.infinix.instalane.utils.AppDialog
import com.infinix.instalane.utils.gone
import com.infinix.instalane.utils.visible
import com.tbruyelle.rxpermissions3.RxPermissions
import java.util.*


class MapsActivity : ActivityAppBase(), OnMapReadyCallback {

    private val viewModel by lazy {
        ViewModelProvider(this)[MapViewModel::class.java].apply {
            nearStoreLiveData.observe(this@MapsActivity, this@MapsActivity::showStores)
            onError.observe(this@MapsActivity) { hideProgressDialog() }
        }
    }
    private val binding by lazy { ActivityMapsBinding.inflate(layoutInflater) }

    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(this@MapsActivity) }
    private lateinit var mMap: GoogleMap
    private val stores = ArrayList<Store>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.mStoreList.gone()
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        googleMap.uiSettings.isMyLocationButtonEnabled = false
        googleMap.uiSettings.isCompassEnabled = false
        googleMap.setOnMarkerClickListener {
            showPreview(it)
            true
        }
        mMap = googleMap
        moveToMyLocation()

        binding.mSearchHere.setOnClickListener {
            binding.mSearchHere.gone()
            clearMapAndGetNewData(mMap.cameraPosition.target)
        }

        mMap.setOnCameraMoveStartedListener {reason->
            Log.i("MAPEVENT", "started int: $reason")
            var reasonText = "UNKNOWN_REASON"
            when (reason) {
                GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE -> {
                    reasonText = "GESTURE"
                    Handler(Looper.myLooper()!!)
                        .postDelayed({
                            binding.mSearchHere.visible()
                        }, 1000)

                }
                GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION -> { reasonText = "API_ANIMATION" }
                GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION -> { reasonText = "DEVELOPER_ANIMATION" }
            }
            Log.i("MAPEVENT", "started int: $reasonText")
        }
    }

    private fun showStores(list:List<Store>){
        val listFilter = list.filter { it.hasLocation() }
        hideProgressDialog()
        stores.addAll(listFilter)
        val adapterMap = StoreMapAdapter(listFilter)
        adapterMap.onItemSelected = {
            val dialog = StoreDialogFragment(it)
            dialog.show(supportFragmentManager, "")
        }

        if (list.isEmpty())
            binding.mStoreList.gone()
        else
            binding.mStoreList.visible()

        binding.mStoreList.apply {
            offscreenPageLimit = 1
            val recyclerView = getChildAt(0) as RecyclerView
            recyclerView.apply {
                val padding = 100
                setPadding(padding, 0, padding, 0)
                clipToPadding = false
            }
            adapter = adapterMap
        }

        binding.mStoreList.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                goToMarker(stores[position])
            }
        })

        listFilter.forEach {
            if (it.hasLocation()){
                if (it.icon.isNullOrEmpty())
                    loadIconStore(it, R.drawable.ic_pin_store)
                else
                    loadIconStore(it, it.icon!!)
            }
        }
    }

    private fun loadIconStore(store: Store, dataToLoad:Any){
        Glide.with(this@MapsActivity)
            .asBitmap().load(dataToLoad)
            .circleCrop()
            .apply(RequestOptions().override(80, 80))
            .into(object : CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val markerOption = MarkerOptions().position(store.getLocation()!!).icon(BitmapDescriptorFactory.fromBitmap(resource))
                    markerOption.title(store.id.toString())
                    mMap.addMarker(markerOption)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    loadIconStore(store, R.drawable.ic_pin_store)
                }
            })
    }

    private fun showPreview(marker: Marker) {

        stores.forEachIndexed { index, store ->
            if (store.id.toString() == marker.title){
                binding.mStoreList.setCurrentItem(index, true)
                goToMarker(store)
            }
        }
    }

    private fun goToMarker(store: Store) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(store.getLocation()!!, 18f))
    }

    @SuppressLint("MissingPermission")
    private fun moveToMyLocation() {
        val disposePermission = RxPermissions(this)
            .request(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).subscribe { granted: Boolean? ->
                if (granted != null && granted) {
                    mMap.isMyLocationEnabled = true
                    val manager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                    val statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    if (!statusOfGPS){
                        AppDialog.showDialog(applicationContext,
                            getString(R.string.app_name),
                            getString(R.string.gps_description),
                            confirm = getString(R.string.turn_on_gps),
                            confirmListener = object : AppDialog.ConfirmListener{
                                override fun onClick() {
                                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                                }
                            }
                        )
                        return@subscribe
                    }

                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { loc->
                            if (loc != null) {
                                SingletonLocation.instance.locationSelected = null
                                if (SingletonLocation.instance.myLocation==null)
                                    SingletonLocation.instance.myLocation = LatLng(loc.latitude, loc.longitude)
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(SingletonLocation.instance.myLocation!!, 12f))
                                clearMapAndGetNewData(SingletonLocation.instance.myLocation!!)
                            }
                        }
                }
            }
    }

    private fun clearMapAndGetNewData(lng: LatLng){
        mMap.clear()
        stores.clear()
        viewModel.getStores(lng, getMapVisibleRadius())
    }

    private fun getMapVisibleRadius(): Double {
        val visibleRegion: VisibleRegion = mMap.projection.visibleRegion
        val diagonalDistance = FloatArray(1)
        val farLeft = visibleRegion.farLeft
        val nearRight = visibleRegion.nearRight
        Location.distanceBetween(
            farLeft.latitude,
            farLeft.longitude,
            nearRight.latitude,
            nearRight.longitude,
            diagonalDistance
        )
        return (diagonalDistance[0] / 2).toDouble()
    }
}