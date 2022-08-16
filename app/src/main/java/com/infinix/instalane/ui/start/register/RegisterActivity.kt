package com.infinix.instalane.ui.start.register

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import com.infinix.instalane.R
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.data.remote.response.User
import com.infinix.instalane.databinding.ActivityRegisterBinding
import com.infinix.instalane.ui.base.ActivityAppBase
import com.infinix.instalane.ui.home.MainActivity
import com.infinix.instalane.ui.homeGuard.MainGuardActivity
import com.infinix.instalane.utils.*
import retrofit2.HttpException
import com.infinix.instalane.utils.transitionButton.TransitionButton


class RegisterActivity : ActivityAppBase() {

    companion object{
        const val ARG_ROLE = "ARG_ROLE"

        fun getIntent(context: Context, role:String) =
            Intent(context, RegisterActivity::class.java)
                .putExtra(ARG_ROLE, role)
    }

    var mRole:String?=null
    private val viewModel by lazy {
        ViewModelProvider(this)[RegisterViewModel::class.java].apply {
            registerLiveData.observe(this@RegisterActivity, this@RegisterActivity::registerResult)
            onError.observe(this@RegisterActivity) {
                binding.mSignUp.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null)
                showErrorMessage(it)
            }
        }
    }

    private val binding by lazy { ActivityRegisterBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        configRegister()
        setSpinner()

        mRole = intent?.getStringExtra(ARG_ROLE)

        binding.tvLogin.setOnClickListener { finish() }
        binding.mSignUp.setOnClickListener { register() }

        binding.etPassword.listenerAfterTextChanged { binding.boxPassword.isPasswordVisibilityToggleEnabled = true }
        binding.etConfirmPassword.listenerAfterTextChanged { binding.boxConfirmPassword.isPasswordVisibilityToggleEnabled = true }
    }

    private fun setSpinner() {
        val spinnerArrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this, R.layout.item_spinner, arrayOf("User", "Guard")
        )
        binding.mSpinnerRole.adapter = spinnerArrayAdapter
    }

    private fun registerResult(result: Result<User>) {
        hideProgressDialog()
        if (result.isSuccess) result.getOrNull()?.let { user -> showMain() }
        else {
            binding.mSignUp.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null)
            if (result.exceptionOrNull()!=null && (result.exceptionOrNull() is HttpException)){
                if ((result.exceptionOrNull() as HttpException).code() == 409){
                    AppDialog.showDialog(this,
                        getString(R.string.app_name),
                        "This email already exists",
                        getString(R.string.sign_in),
                        getString(R.string.ok),
                        confirmListener = object : AppDialog.ConfirmListener{
                            override fun onClick() {
                                onBackPressed()
                            }
                        }
                    )
                }else
                    showErrorMessage(result.exceptionOrNull())
            }else
                showErrorMessage(result.exceptionOrNull())
        }
    }

    private fun register() {
        binding.etFullName.error = null
        binding.etEmail.error = null
        binding.etPassword.error = null
        binding.etConfirmPassword.error = null
        binding.etPhoneNumber.error = null

        val fullName = binding.etFullName.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()
        val phoneNumber = binding.etPhoneNumber.text.toString()

        when {
            fullName.isBlank() -> binding.etFullName.error = getString(R.string.field_required)
            email.isBlank() -> binding.etEmail.error = getString(R.string.field_required)
            !isValidEmail(email) -> binding.etEmail.error = getString(R.string.invalid_email)
            password.isBlank() -> {
                binding.boxPassword.isPasswordVisibilityToggleEnabled = false
                binding.etPassword.error = getString(R.string.field_required)
            }
            confirmPassword.isBlank() -> {
                binding.boxConfirmPassword.isPasswordVisibilityToggleEnabled = false
                binding.etConfirmPassword.error = getString(R.string.field_required)
            }
            password != confirmPassword -> { showMessage(getString(R.string.not_match_password)) }
            phoneNumber.isBlank() -> binding.etPhoneNumber.error = getString(R.string.field_required)
            !binding.mSwitchAccept.isChecked -> showMessage(getString(R.string.error_accept_terms))

            else -> {
                //showProgressDialog()
                hideKeyboard()
                binding.mSignUp.startAnimation()
                viewModel.register(fullName, email, password, phoneNumber, mRole!!)
            }
        }
    }

    private fun showMain() {
        binding.mSignUp.apply {
            margins(left = 0, right = 0)
            stopAnimation(TransitionButton.StopAnimationStyle.EXPAND) {
                if (AppPreferences.getUser()?.isUser()==true)
                    showMainUser()
                else
                    showMainGuard()
            }
        }
    }

    private fun showMainUser() {
        hideProgressDialog()
        startActivity(
            Intent(this, MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    private fun showMainGuard() {
        hideProgressDialog()
        startActivity(
            Intent(this, MainGuardActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun configRegister() {
        val completeText = getString(R.string.i_have_an_account_sign_in)
        val register = getString(R.string.sign_in)

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

        binding.tvLogin.setText(spannable, TextView.BufferType.SPANNABLE)
    }
}