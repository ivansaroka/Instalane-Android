package com.infinix.instalane.ui.start.legal

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Legal
import com.infinix.instalane.databinding.ActivityLegalBinding
import com.infinix.instalane.ui.base.ActivityAppBase
import com.infinix.instalane.ui.start.register.RegisterViewModel

class LegalActivity : ActivityAppBase() {

    companion object{
        private const val ARG_IS_PRIVACY_POLICY = "ARG_IS_PRIVACY_POLICY"

        fun getIntent(context: Context, isPrivacy :Boolean)=
            Intent(context, LegalActivity::class.java).apply {
                putExtra(ARG_IS_PRIVACY_POLICY, isPrivacy) }
    }

    private val viewModel by lazy {
        ViewModelProvider(this)[RegisterViewModel::class.java].apply {
            privacyLiveData.observe(this@LegalActivity, this@LegalActivity::showData)
            termsLiveData.observe(this@LegalActivity, this@LegalActivity::showData)
            onError.observe(this@LegalActivity) {
                hideProgressDialog()
                //showErrorAlert(it)
            }
        }
    }

    private val binding by lazy { ActivityLegalBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        intent.getBooleanExtra(ARG_IS_PRIVACY_POLICY, false).let { isPrivacy->
            if (isPrivacy){
                setToolbar(getString(R.string.privacy_policy))
                viewModel.getPrivacy()
            }
            else{
                setToolbar(getString(R.string.terms_and_conditions))
                viewModel.getTerms()
            }
        }

    }

    private fun showData(legal:Legal){
        binding.mDescription.text = legal.description
    }
}