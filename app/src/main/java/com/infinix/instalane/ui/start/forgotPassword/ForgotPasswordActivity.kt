package com.infinix.instalane.ui.start.forgotPassword

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.infinix.instalane.R
import com.infinix.instalane.databinding.ActivityForgotPasswordBinding
import com.infinix.instalane.ui.base.ActivityAppBase
import com.infinix.instalane.ui.start.login.LoginViewModel
import com.infinix.instalane.utils.listenerAfterTextChanged
import com.infinix.instalane.utils.showErrorMessage
import com.infinix.instalane.utils.showMessage

class ForgotPasswordActivity : ActivityAppBase() {

    private val viewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java].apply {
            forgetPasswordLiveData.observe(this@ForgotPasswordActivity, this@ForgotPasswordActivity::onSuccess)
            onError.observe(this@ForgotPasswordActivity) {
                hideProgressDialog()
                showErrorMessage(it)
            }
        }
    }

    private val binding by lazy { ActivityForgotPasswordBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setToolbar(getString(R.string.forgot_password_title))

        binding.etEmail.listenerAfterTextChanged { binding.mSend.isEnabled = it.isNotEmpty() }

        binding.mSend.setOnClickListener {
            showProgressDialog()
            viewModel.forgotPassword(binding.etEmail.text.toString())
        }

    }

    private fun onSuccess(any: Any){
        hideProgressDialog()
        showMessage("The email was sent")
        finish()
    }
}