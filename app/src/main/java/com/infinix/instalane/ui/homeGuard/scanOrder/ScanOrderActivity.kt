package com.infinix.instalane.ui.homeGuard.scanOrder

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.gms.vision.barcode.Barcode
import com.infinix.instalane.databinding.ActivityCouponLectorBinding
import com.infinix.instalane.ui.base.ActivityAppBase
import com.infinix.instalane.ui.homeGuard.purchaseSummary.PurchaseSummaryGuardActivity
import com.infinix.instalane.utils.showMessage
import com.tbruyelle.rxpermissions3.RxPermissions
import io.reactivex.android.schedulers.AndroidSchedulers


class ScanOrderActivity : ActivityAppBase() {

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
        sendResultCode(barcode.displayValue)
    }

    private fun sendResultCode(value :String) {
        startActivity(PurchaseSummaryGuardActivity.getIntent(this, value))
        finish()
    }

}