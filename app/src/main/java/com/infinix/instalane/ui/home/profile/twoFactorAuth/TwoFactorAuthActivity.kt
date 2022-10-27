package com.infinix.instalane.ui.home.profile.twoFactorAuth

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.infinix.instalane.R
import com.infinix.instalane.databinding.ActivityTwoFactorAuthBinding
import com.infinix.instalane.ui.base.ActivityAppBase
import com.infinix.instalane.ui.home.profile.myShopping.MyShoppingViewModel
import com.infinix.instalane.utils.showErrorMessage
import com.infinix.instalane.utils.showFragment

class TwoFactorAuthActivity : ActivityAppBase() {

    private val viewModel by lazy {
        ViewModelProvider(this)[TwoFactorAuthViewModel::class.java].apply {
            sendCodeLiveData.observe(this@TwoFactorAuthActivity) { goToValidateCode() }
            validateCodeLiveData.observe(this@TwoFactorAuthActivity, {})
            onError.observe(this@TwoFactorAuthActivity) {
                hideProgressDialog()
                showErrorMessage(it)
            }
        }
    }

    private val binding by lazy { ActivityTwoFactorAuthBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setToolbar(getString(R.string._2_factor_authentication))

        showFragment(binding.container.id, SendCodeFragment(), "")
    }

    fun sendCode(phone:String){
        showProgressDialog()
        viewModel.sendCode(phone)
    }

    fun validateCode(code:String){
        showProgressDialog()
        viewModel.validateCode(code)
    }

    private fun goToValidateCode(){
        hideProgressDialog()
        showFragment(binding.container.id, ValidateCodeFragment(), "")
    }

    override fun onBackPressed() {
        when (supportFragmentManager.findFragmentById(binding.container.id)) {
            is SendCodeFragment -> super.onBackPressed()
            is ValidateCodeFragment -> showFragment(binding.container.id, SendCodeFragment(), "")
        }
    }

}