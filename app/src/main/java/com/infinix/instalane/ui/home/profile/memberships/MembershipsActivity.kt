package com.infinix.instalane.ui.home.profile.memberships

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Memberships
import com.infinix.instalane.databinding.ActivityMembershipsBinding
import com.infinix.instalane.ui.base.ActivityAppBase
import com.infinix.instalane.ui.base.EmptyAdapter
import com.infinix.instalane.ui.home.profile.memberships.addMembership.AddMembershipActivity

class MembershipsActivity : ActivityAppBase() {

    private val viewModel by lazy {
        ViewModelProvider(this)[MembershipViewModel::class.java].apply {
            membershipsLiveData.observe(this@MembershipsActivity, this@MembershipsActivity::showData)
            onError.observe(this@MembershipsActivity) { showErrorApi(it) }
        }
    }

    private val binding by lazy { ActivityMembershipsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setToolbar(getString(R.string.memberships))
        binding.mConfirm.setOnClickListener { resultLauncher.launch(Intent(this, AddMembershipActivity::class.java)) }

        showProgressDialog()
        viewModel.getMemberships()
    }

    private fun showData(list:List<Memberships>){
        hideProgressDialog()
        if (list.isEmpty())
            binding.mList.adapter = EmptyAdapter(getString(R.string.empty_membership))
        else
            binding.mList.adapter = MembershipAdapter(list)
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            showProgressDialog()
            viewModel.getMemberships()
        }
    }
}