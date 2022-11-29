package com.infinix.instalane.ui.start.login

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.infinix.instalane.R
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.databinding.ActivityLoginBinding
import com.infinix.instalane.ui.base.ActivityAppBase
import com.infinix.instalane.ui.home.MainActivity
import com.infinix.instalane.ui.home.profile.twoFactorAuth.TwoFactorAuthActivity
import com.infinix.instalane.ui.homeGuard.MainGuardActivity
import com.infinix.instalane.ui.start.forgotPassword.ForgotPasswordActivity
import com.infinix.instalane.ui.start.register.SelectRoleActivity
import com.infinix.instalane.utils.*
import com.infinix.instalane.utils.transitionButton.TransitionButton


class LoginActivity : ActivityAppBase() {

    private val viewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java].apply {
            loginLiveData.observe(this@LoginActivity, this@LoginActivity::loginResult)
            onErrorLogin.observe(this@LoginActivity) {
                binding.confirm.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null)
                showErrorMessage(it)
            }
            loginSocialNetLiveData.observe(this@LoginActivity, this@LoginActivity::loginResultSocialNet)
            onError.observe(this@LoginActivity) {
                hideProgressDialog()
                showErrorMessage(it)
            }
        }
    }

    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private lateinit var callbackManager : CallbackManager
    private lateinit var profileTracker : ProfileTracker
    private val PERMISSIONS = arrayOf("email", "public_profile")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        setContentView(binding.root)
        getRemoteConfig()
        configRegister()
        binding.tvRegister.setOnClickListener { startActivity(Intent(this, SelectRoleActivity::class.java)) }
        binding.confirm.setOnClickListener { login() }
        binding.btnGoogle.setOnClickListener { loginWithGoogle() }
        binding.mForgotPassword.setOnClickListener { startActivity(Intent(this, ForgotPasswordActivity::class.java)) }

        callbackManager = CallbackManager.Factory.create()
        profileTracker = object : ProfileTracker() {
            override fun onCurrentProfileChanged(
                oldProfile: Profile?,
                currentProfile: Profile?
            ) {
                Log.i("TAGFACEBOOK", "ProfileTracker")
                Log.i("TAGFACEBOOK", "currentProfile : ${currentProfile?.firstName}")
            }
        }
        binding.loginButton.setPermissions(PERMISSIONS.toList())
        binding.loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onCancel() {
                Log.i("TAGFACEBOOK", "onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.i("TAGFACEBOOK", "onError: ${error.message}")
            }

            override fun onSuccess(result: LoginResult?) {
                Log.i("TAGFACEBOOK", "onSuccess")
                viewModel.loginWithFacebook(result!!.accessToken.userId, result.accessToken.token)
            }
        })
        binding.btnFacebook.setOnClickListener { binding.loginButton.performClick() }
        binding.etPassword.listenerAfterTextChanged { binding.boxPassword.isPasswordVisibilityToggleEnabled = true }
        binding.mForgotPassword.setOnClickListener { startActivity(Intent(this, ForgotPasswordActivity::class.java)) }
    }

    private fun login(){
        binding.etEmail.error = null
        binding.etPassword.error = null

        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        when {
            email.isBlank() -> binding.etEmail.error = getString(R.string.field_required)
            !isValidEmail(email) -> binding.etEmail.error = getString(R.string.invalid_email)
            password.isBlank() -> {
                binding.boxPassword.isPasswordVisibilityToggleEnabled = false
                binding.etPassword.error = getString(R.string.field_required)
            }
            else -> {
                //showProgressDialog()
                hideKeyboard()
                binding.confirm.startAnimation()
                viewModel.login(binding.etEmail.text.toString(), binding.etPassword.text.toString())
            }
        }
    }

    private fun loginWithGoogle() {
        signInWithGoogle { account, sToken->
            showProgressDialog()
            viewModel.loginWithGoogle(account)
        }
    }

    private fun loginResult(result: Any) {
        binding.btnGoogle.elevation = 0f
        binding.btnFacebook.elevation = 0f
        binding.confirm.apply {
            margins(left = 0, right = 0)
            stopAnimation(TransitionButton.StopAnimationStyle.EXPAND) {
                showMain()
            }
        }
    }

    private fun loginResultSocialNet(result: Any) { showMain() }

    private fun showMain() {
        hideProgressDialog()
        if (AppPreferences.getUser()?.isUser()==true){
            if (AppPreferences.getUser()!!.twofactor==true){
                result2FactorAuthLauncher.launch(TwoFactorAuthActivity.getIntent(this, true))
                Handler(Looper.myLooper()!!).postDelayed({
                    binding.confirm.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null)
                }, 200)

            } else
                showMainUser()
        }
        else
            showMainGuard()
    }

    private fun showMainUser() {
        hideProgressDialog()
        startActivity(
            Intent(this, MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    private var result2FactorAuthLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            showMainUser()
        }
    }

    private fun showMainGuard() {
        hideProgressDialog()
        startActivity(
            Intent(this, MainGuardActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    private fun configRegister() {
        val completeText = getString(R.string.i_don_t_have_a_account_sign_up)
        val register = getString(R.string.sign_up)

        val spannable = SpannableStringBuilder(completeText)
        spannable.setSpan(
            ForegroundColorSpan(getColor(R.color.colorPrimary)),
            completeText.indexOf(register), completeText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val font = ResourcesCompat.getFont(this, R.font.manrope_semi_bold)
        spannable.setSpan(
            CustomTypefaceSpan(Typeface.create(font, Typeface.BOLD)),
            completeText.indexOf(register), completeText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvRegister.setText(spannable, TextView.BufferType.SPANNABLE)
    }

    private fun getRemoteConfig() {
        if (AppPreferences.getClientSecret()?.isEmpty() == true) {
            val remoteConfig = Firebase.remoteConfig
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds = 3600
            }
            remoteConfig.setConfigSettingsAsync(configSettings)
            remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
            remoteConfig.fetchAndActivate()
                .addOnCompleteListener(this) { task ->

                    val clientSecret = remoteConfig["client_secret"].asString()
                    if (clientSecret != "key1")
                        AppPreferences.setClientSecret(clientSecret)

                    val serverClientId = remoteConfig["server_client_id"].asString()
                    if (serverClientId != "key2")
                        AppPreferences.setServerClientId(serverClientId)

                    val serverKey = remoteConfig["google_place_server_key"].asString()
                    if (serverKey != "key3")
                        AppPreferences.setGoogleServerPlaceKey(serverKey)
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        profileTracker.stopTracking()
    }
}