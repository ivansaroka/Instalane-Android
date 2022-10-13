package com.infinix.instalane.ui.home.barcode

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.vision.barcode.Barcode
import com.google.gson.Gson
import com.infinix.instalane.R
import com.infinix.instalane.data.SingletonProduct
import com.infinix.instalane.data.remote.response.Product
import com.infinix.instalane.data.remote.response.Store
import com.infinix.instalane.databinding.ActivityBarCodeBinding
import com.infinix.instalane.ui.base.ActivityAppBase
import com.infinix.instalane.ui.home.checkout.CheckoutActivity
import com.infinix.instalane.utils.AppDialog
import com.infinix.instalane.utils.showMessage
import com.tbruyelle.rxpermissions3.RxPermissions
import io.reactivex.android.schedulers.AndroidSchedulers


class BarCodeActivity : ActivityAppBase() {

    companion object{
        private const val ARG_STORE = "ARG_STORE"

        fun getIntent(context: Context, store: Store)=
            Intent(context, BarCodeActivity::class.java).apply {
                putExtra(ARG_STORE, Gson().toJson(store)) }
    }

    private val viewModel by lazy {
        ViewModelProvider(this)[ProductViewModel::class.java].apply {
            productLiveData.observe(this@BarCodeActivity, this@BarCodeActivity::showScannedProduct)
            onError.observe(this@BarCodeActivity) {
                hideProgressDialog()
                qrScanned = false
                showErrorApi(it)
            }
        }
    }

    private val binding by lazy { ActivityBarCodeBinding.inflate(layoutInflater) }
    private var qrScanned = false
    private var mStore:Store?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        SingletonProduct.instance.clearList()
        setupScanner()
        intent.getStringExtra(ARG_STORE)?.let { mStore = Gson().fromJson(it, Store::class.java) }

        binding.mBack.setOnClickListener { onBackPressed() }
        binding.mEnterCode.setOnClickListener { showEnterCodeDialog() }

        val adapter = ProductAdapter(ArrayList(), mStore, isBasket = true)
        adapter.onRemove={ showRemoveAlert(it) }
        binding.mContSummary.mList.adapter = adapter

        binding.mContSummary.mConfirm.setOnClickListener {
            val list = (binding.mContSummary.mList.adapter as ProductAdapter).list
            if (list.isEmpty()){
                showMessage("You must add at least one product to your basket")
                return@setOnClickListener
            }
            SingletonProduct.instance.productList = list
            SingletonProduct.instance.store = mStore!!
            val intent = CheckoutActivity.getIntent(this, mStore!!)
            resultLauncher.launch(intent)
        }
    }

    override fun onBackPressed() {
        AppDialog.showDialog(this@BarCodeActivity, getString(R.string.app_name),
            "Are you sure you want to go back? This action will clean your list of scanned products.",
            getString(R.string._yes), getString(R.string._no), confirmListener = object :
                AppDialog.ConfirmListener {
                override fun onClick() {
                    super@BarCodeActivity.onBackPressed()
                }
            })
    }

    private fun showRemoveAlert(product: Product){
        AppDialog.showDialog(this,
            getString(R.string.app_name),
            "Do you want to delete this item?", getString(R.string.remove), getString(R.string.cancel), confirmListener = object :
                AppDialog.ConfirmListener {
                override fun onClick() {
                    (binding.mContSummary.mList.adapter as ProductAdapter).removeProduct(product)
                    checkSubtotal()
                }
            })
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK)
            finish()
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
        showProgressDialog()
        viewModel.getProduct(barcode.displayValue, mStore!!)
    }

    private fun showScannedProduct(product: Product){
        hideProgressDialog()
        qrScanned = true
        val dialog = ScannedProductDialogFragment(product, mStore!!)
        dialog.onAdd = {
            qrScanned = false
            (binding.mContSummary.mList.adapter as ProductAdapter).addProduct(product)
            checkSubtotal()
        }
        dialog.onCancel = { qrScanned = false }
        dialog.show(supportFragmentManager, "")
    }

    private fun checkSubtotal(){
        val totalList = (binding.mContSummary.mList.adapter as ProductAdapter).list
        var subtotal = 0f
        totalList.forEach { prod->
                subtotal+= prod.getPriceByRegion(mStore?.region)
        }
        binding.mContSummary.mTotal.text = "$${String.format("%.2f", subtotal)}"
        binding.mTotalResume2.text = "$${String.format("%.2f", subtotal)}"
    }

    private fun showEnterCodeDialog(){
        qrScanned = true
        val dialog = EnterCodeDialogFragment(mStore!!)
        dialog.onConfirm = { product-> showScannedProduct(product) }
        dialog.onCancel = { qrScanned = false }
        dialog.show(supportFragmentManager, "")
    }

}