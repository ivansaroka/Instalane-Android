package com.infinix.instalane.ui.home.profile.twoFactorAuth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.infinix.instalane.R
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.databinding.ActivityTwoFactorAuthBinding
import com.infinix.instalane.ui.base.ActivityAppBase
import com.infinix.instalane.utils.showErrorMessage
import com.infinix.instalane.utils.showFragment

class TwoFactorAuthActivity : ActivityAppBase() {

    companion object{
        const val ARG_FROM_LOGIN = "ARG_FROM_LOGIN"

        fun getIntent(context: Context, isFromLogin: Boolean)=
            Intent(context, TwoFactorAuthActivity::class.java).apply {
                putExtra(ARG_FROM_LOGIN, isFromLogin)
            }
    }

    private val viewModel by lazy {
        ViewModelProvider(this)[TwoFactorAuthViewModel::class.java].apply {
            sendCodeLiveData.observe(this@TwoFactorAuthActivity) { goToValidateCode() }
            validateCodeLiveData.observe(this@TwoFactorAuthActivity, { onSuccess() })
            onError.observe(this@TwoFactorAuthActivity) {
                hideProgressDialog()
                showErrorMessage(it)
            }
        }
    }

    private val binding by lazy { ActivityTwoFactorAuthBinding.inflate(layoutInflater) }
    private var phoneNumber :String?=null
    private var accessToken :String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setToolbar(getString(R.string._2_factor_authentication))

        accessToken = AppPreferences.getUser()?.accessToken

        var isFromLogin = false
        intent?.getBooleanExtra(ARG_FROM_LOGIN, false)?.apply {
            isFromLogin = this
        }

        val fragment = SendCodeFragment()
        val bundle = Bundle()
        bundle.putBoolean(ARG_FROM_LOGIN, isFromLogin)
        fragment.arguments = bundle

        showFragment(binding.container.id, fragment, "")
    }

    fun sendCode(phone:String){
        phoneNumber = phone
        showProgressDialog()
        viewModel.sendCode(phone,accessToken!!)
    }

    fun validateCode(code:String){
        showProgressDialog()
        viewModel.validateCode(code,accessToken!!)
    }

    private fun onSuccess(){
        val intent = Intent()
        intent.putExtra("phone", phoneNumber)
        setResult(Activity.RESULT_OK, intent)
        finish()
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