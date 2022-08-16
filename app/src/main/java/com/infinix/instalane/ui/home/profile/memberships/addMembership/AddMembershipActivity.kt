package com.infinix.instalane.ui.home.profile.memberships.addMembership

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Memberships
import com.infinix.instalane.databinding.ActivityAddMembershipBinding
import com.infinix.instalane.ui.base.ActivityAppBase
import com.infinix.instalane.ui.home.profile.memberships.MembershipViewModel
import com.infinix.instalane.utils.AppDialog
import com.infinix.instalane.utils.listenerAfterTextChanged

class AddMembershipActivity : ActivityAppBase() {

    private val viewModel by lazy {
        ViewModelProvider(this)[MembershipViewModel::class.java].apply {
            addMembershipsLiveData.observe(this@AddMembershipActivity, this@AddMembershipActivity::onSuccess)
            companyLiveData.observe(this@AddMembershipActivity, this@AddMembershipActivity::showCompanies)
            onError.observe(this@AddMembershipActivity) {
                hideProgressDialog()
                showErrorApi(it) }
        }
    }

    private val binding by lazy { ActivityAddMembershipBinding.inflate(layoutInflater) }
    var mCompany:Memberships.Company?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setToolbar(getString(R.string.add_memberships))

        binding.mNumber.listenerAfterTextChanged { checkButton() }
        binding.mFirstName.listenerAfterTextChanged { checkButton() }
        binding.mLastName.listenerAfterTextChanged { checkButton() }
        binding.mCompanySelector.setOnClickListener { showStoreDialog() }

        binding.mConfirm.setOnClickListener {
            showProgressDialog()
            viewModel.addMembership(binding.mNumber.text.toString(), binding.mFirstName.text.toString(), binding.mLastName.text.toString(), mCompany!!.id!!)
        }

        showProgressDialog()
        viewModel.getCompanies()
    }

    private fun showStoreDialog(){
        val dialog = SelectCompanyDialogFragment()
        dialog.onStoreSelected = {
            binding.mCompanySelector.setText(it.name)
        }
        dialog.show(supportFragmentManager, null)
    }

    private fun checkButton() {
        binding.mConfirm.isEnabled = !binding.mNumber.text.isNullOrEmpty()
                && !binding.mFirstName.text.isNullOrEmpty()
                && !binding.mLastName.text.isNullOrEmpty()
                && mCompany != null
    }

    private fun showCompanies(list:List<Memberships.Company>) {
        hideProgressDialog()
        if (list.isNotEmpty()) {
            val sortedList = list.sortedBy { it.name }
            mCompany = sortedList[0]
            val sortedListName = list.map { it.name }.sortedBy { it }
            val spinnerArrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, R.layout.item_spinner,  sortedListName)
            binding.mSpinnerCompany.adapter = spinnerArrayAdapter
            binding.mSpinnerCompany.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) { mCompany = sortedList[pos] }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }
    }

    private fun onSuccess(membership : Memberships ){
        hideProgressDialog()
        AppDialog.showDialog(this,
            title = getString(R.string.title_membership_success),
            body = getString(R.string.description_membership_success),
            confirm = getString(R.string.ok),
            confirmListener = object : AppDialog.ConfirmListener{
                override fun onClick() {
                    val intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        )
    }
}