package com.infinix.instalane.ui.homeGuard.purchaseSummary

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Note
import com.infinix.instalane.data.remote.response.Order
import com.infinix.instalane.databinding.ActivityPurchaseSummaryGuardBinding
import com.infinix.instalane.ui.base.ActivityAppBase
import com.infinix.instalane.ui.home.checkout.paymentResult.PaymentSuccessfulActivity
import com.infinix.instalane.ui.home.checkout.purchaseSummary.PurchaseViewModel

class PurchaseSummaryGuardActivity : ActivityAppBase() {

    companion object{
        const val ORDER_ID = "ORDER_ID"

        fun getIntent(context: Context, orderId : String) =
            Intent(context, PurchaseSummaryGuardActivity::class.java)
            .apply { putExtra(ORDER_ID, orderId) }
    }

    var mNote :String?=null
    var mStatus :Int?=null

    private val viewModel by lazy {
        ViewModelProvider(this)[PurchaseViewModel::class.java].apply {
            addNoteLiveData.observe(this@PurchaseSummaryGuardActivity){onAddNoteSuccess()}
            orderLiveData.observe(this@PurchaseSummaryGuardActivity, this@PurchaseSummaryGuardActivity::showData)
            onError.observe(this@PurchaseSummaryGuardActivity) {
                hideProgressDialog()
                showErrorAlertString(it)
            }
            confirmOrderLiveData.observe(this@PurchaseSummaryGuardActivity) { onSuccess() }
        }
    }

    private val binding by lazy { ActivityPurchaseSummaryGuardBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setToolbar(getString(R.string.purchase_summary))

        binding.mConfirm.setOnClickListener { orderConfirmed() }
        binding.mAddNote.setOnClickListener { showEnterNoteDialog() }
        binding.mConfirm.isEnabled = false
        showProgressDialog()
        intent.getStringExtra(ORDER_ID).let { viewModel.getOrder(it!!) }

    }

    private fun showData(order: Order){
        hideProgressDialog()

        if(order.status == Order.STATE_DELIVERED) {
            startActivity(OrderErrorActivity.getIntent(this, order))
            finish()
            return
        }

        binding.mList.adapter = ProductGuardAdapter(order.items!!.filter { it.product!=null }){
            if (it.isEmpty()){
                binding.mConfirm.isEnabled = false
                binding.mConfirm.text = getString(R.string.confirm_order)
            } else {
                binding.mConfirm.isEnabled = true
                if (it.size == 1)
                    binding.mConfirm.text = "${getString(R.string.confirm_order)} - 1 Item"
                else
                    binding.mConfirm.text = "${getString(R.string.confirm_order)} - ${it.size} Items"
            }
        }

        if (!order.notes.isNullOrEmpty()){
            mNote = order.notes!![0].note
            mStatus = order.notes!![0].status
            showStatus(mStatus!!)
        }
    }

    private fun orderConfirmed() {
        showProgressDialog()
        intent.getStringExtra(ORDER_ID).let { orderId ->
            showProgressDialog()
            val items = (binding.mList.adapter as ProductGuardAdapter).listSelected
            viewModel.confirmOrder(orderId!!, items)
        }
    }

    private fun onSuccess() {
        hideProgressDialog()
        startActivity(Intent(this, PaymentSuccessfulActivity::class.java))
        finish()
    }

    private fun onAddNoteSuccess(){
        hideProgressDialog()
        Toast.makeText(this, "Note added successfully", Toast.LENGTH_LONG).show()
    }

    private fun showEnterNoteDialog() {
        intent.getStringExtra(ORDER_ID).let { orderId->
            val dialog = EnterNoteDialogFragment(mNote, mStatus)
            dialog.onConfirm = { note, status->
                mNote = note
                mStatus = status
                showStatus(status)
                showProgressDialog()
                viewModel.addNote(orderId!!, note, status)
            }
            dialog.show(supportFragmentManager, "")
        }
    }

    private fun showStatus(status:Int){
        //binding.mCancel.visibility = View.VISIBLE
        binding.mStatus.visibility = View.VISIBLE
        when(status){
            Note.ALL_SCANNED_PAID -> {
                binding.mConfirm.isEnabled = true
                binding.mStatus.setImageResource(R.drawable.circle_green)
                binding.mConfirm.setBackgroundResource(R.drawable.bg_button_green)
            }
            Note.ONE_ITEM_NOT_SCANNED_PAID-> {
                binding.mConfirm.isEnabled = true
                binding.mStatus.setImageResource(R.drawable.circle_yellow)
                binding.mConfirm.setBackgroundResource(R.drawable.bg_button_yellow)
            }
            Note.MORE_ITEM_NOT_SCANNED_PAID-> {
                binding.mConfirm.isEnabled = true
                binding.mStatus.setImageResource(R.drawable.circle_red)
                binding.mConfirm.setBackgroundResource(R.drawable.bg_button_red)
            }
        }

        binding.mCancel.setOnClickListener {
            mNote = null
            mStatus = null
            binding.mCancel.visibility = View.GONE
            binding.mStatus.visibility = View.GONE

            binding.mConfirm.setBackgroundResource(R.drawable.selector_button)
            binding.mConfirm.isEnabled = false

        }
    }
}