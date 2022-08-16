package com.infinix.instalane.ui.home.checkout.couponLector

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.vision.barcode.Barcode
import com.google.gson.Gson
import com.infinix.instalane.data.remote.response.Coupon
import com.infinix.instalane.data.remote.response.Store
import com.infinix.instalane.databinding.ActivityCouponLectorBinding
import com.infinix.instalane.ui.base.ActivityAppBase
import com.infinix.instalane.utils.showMessage
import com.tbruyelle.rxpermissions3.RxPermissions
import io.reactivex.android.schedulers.AndroidSchedulers


class CouponLectorActivity : ActivityAppBase() {

    companion object{
        private const val ARG_STORE = "ARG_STORE"

        fun getIntent(context: Context, store: Store)=
            Intent(context, CouponLectorActivity::class.java).apply {
                putExtra(ARG_STORE, Gson().toJson(store)) }
    }

    private val viewModel by lazy {
        ViewModelProvider(this)[CouponViewModel::class.java].apply {
            couponLiveData.observe(this@CouponLectorActivity, this@CouponLectorActivity::showScannedCoupon)
            onError.observe(this@CouponLectorActivity) {
                hideProgressDialog()
                qrScanned = false
                showErrorApi(it)
            }
        }
    }

    private val binding by lazy { ActivityCouponLectorBinding.inflate(layoutInflater) }
    private var qrScanned = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupScanner()
        binding.mBack.setOnClickListener { finish() }
    }

    private fun setupScanner() {
        val disposePermission = RxPermissions(this)
            .request(
                Manifest.permission.CAMERA
            ).subscribe { granted: Boolean? ->
                if (granted != null && granted) {
                    val mDisposable = binding.scannerView
                        .getObservable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::showInfo, this::showError)
                }
            }
    }

    private fun showError(throwable: Throwable) = showMessage("Camera initialization error: ${throwable.message}")

    private fun showInfo(barcode: Barcode) {
        if (qrScanned) return
        qrScanned = true
        intent.getStringExtra(ARG_STORE)?.let {
            showProgressDialog()
            val store = Gson().fromJson(it, Store::class.java)
            viewModel.getCoupon(store.id!!, barcode.displayValue)
        }
    }

    private fun showScannedCoupon(coupon: Coupon){
        hideProgressDialog()
        val dialog = ScannedCouponDialogFragment(coupon)
        dialog.onAdd = { sendResultCode(coupon) }
        dialog.onCancel = { qrScanned = false }
        dialog.show(supportFragmentManager, "")
    }

    private fun sendResultCode(coupon: Coupon){
        val intent = Intent()
        intent.putExtra( "COUPON", Gson().toJson(coupon))
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

}