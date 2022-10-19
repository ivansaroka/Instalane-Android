package com.infinix.instalane.ui.home.profile.changePassword

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.infinix.instalane.R
import com.infinix.instalane.databinding.ActivityChangePasswordBinding
import com.infinix.instalane.ui.base.ActivityAppBase
import com.infinix.instalane.ui.start.login.LoginViewModel
import com.infinix.instalane.utils.hideKeyboard
import com.infinix.instalane.utils.listenerAfterTextChanged
import com.infinix.instalane.utils.showErrorMessage
import com.infinix.instalane.utils.showMessage

class ChangePasswordActivity : ActivityAppBase() {

    private val viewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java].apply {
            changePasswordLiveData.observe(this@ChangePasswordActivity, this@ChangePasswordActivity::onSuccess)
            onError.observe(this@ChangePasswordActivity) {
                hideProgressDialog()
                showErrorMessage(it)
            }
        }
    }

    private val binding by lazy { ActivityChangePasswordBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setToolbar(getString(R.string.change_password))

        binding.etPassword.listenerAfterTextChanged { binding.boxPassword.isPasswordVisibilityToggleEnabled = true }
        binding.etConfirmPassword.listenerAfterTextChanged { binding.boxConfirmPassword.isPasswordVisibilityToggleEnabled = true }
        binding.mConfirm.setOnClickListener { verifyPassword() }
    }

    private fun verifyPassword(){
        binding.etPassword.error = null
        binding.etConfirmPassword.error = null

        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        when {
            password.isBlank() -> {
                binding.boxPassword.isPasswordVisibilityToggleEnabled = false
                binding.etPassword.error = getString(R.string.field_required)
            }
            confirmPassword.isBlank() -> {
                binding.boxConfirmPassword.isPasswordVisibilityToggleEnabled = false
                binding.etConfirmPassword.error = getString(R.string.field_required)
            }
            password != confirmPassword -> { showMessage(getString(R.string.not_match_password)) }
            else -> {
                hideKeyboard()
                showProgressDialog()
                viewModel.changePassword(binding.etPassword.text.toString())
            }
        }
    }

    private fun onSuccess(any: Any){
        hideProgressDialog()
        showMessage("Your password was changed")
        finish()
    }
}