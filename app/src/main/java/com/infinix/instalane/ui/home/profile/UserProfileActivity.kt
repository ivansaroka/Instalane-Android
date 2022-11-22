package com.infinix.instalane.ui.home.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.infinix.instalane.R
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.databinding.ActivityUserProfileBinding
import com.infinix.instalane.ui.base.ActivityAppBase
import com.infinix.instalane.ui.home.profile.changePassword.ChangePasswordActivity
import com.infinix.instalane.ui.home.profile.editProfile.EditProfileActivity
import com.infinix.instalane.ui.home.profile.memberships.MembershipsActivity
import com.infinix.instalane.ui.home.profile.myShopping.MyShoppingActivity
import com.infinix.instalane.ui.home.profile.paymentMethods.PaymentMethodsActivity
import com.infinix.instalane.ui.home.profile.twoFactorAuth.TwoFactorAuthActivity
import com.infinix.instalane.ui.start.login.LoginActivity
import com.infinix.instalane.utils.AppDialog

class UserProfileActivity : ActivityAppBase() {

    private val viewModel by lazy {
        ViewModelProvider(this)[UserProfileViewModel::class.java].apply {
            logoutLiveData.observe(this@UserProfileActivity) {}
            deleteLiveData.observe(this@UserProfileActivity) { goOut() }
            onError.observe(this@UserProfileActivity) {
                hideProgressDialog()
                showErrorAlert(it)
            }
        }
    }

    private val binding by lazy { ActivityUserProfileBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setToolbar()

        binding.mEdit.setOnClickListener { startActivity(Intent(this, EditProfileActivity::class.java)) }

        binding.mContPayment.setOnClickListener { startActivity(Intent(this, PaymentMethodsActivity::class.java)) }
        binding.mContMyShopping.setOnClickListener { startActivity(Intent(this, MyShoppingActivity::class.java)) }
        binding.mContMemberships.setOnClickListener { startActivity(Intent(this, MembershipsActivity::class.java)) }
        binding.mContPassword.setOnClickListener { startActivity(Intent(this, ChangePasswordActivity::class.java)) }
        binding.mContDelete.setOnClickListener {
            if (AppPreferences.hasBiometric())
                showBiometricDialog( { delete() }, { } )
            else
                delete()
        }
        binding.mLogout.setOnClickListener { logout() }

        binding.mSwitchFaceId.isChecked = AppPreferences.hasBiometric()
        binding.mSwitchFaceId.setOnClickListener { openBiometricDialog() }

        binding.mContAuth.setOnClickListener { result2FactorAuthLauncher.launch(TwoFactorAuthActivity.getIntent(this, false)) }
    }

    private var result2FactorAuthLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.getStringExtra("phone")?.apply {
                viewModel.setTwoFactor(true, this, AppPreferences.getUser()!!.accessToken!!)
            }
            AppDialog.showDialog(this@UserProfileActivity,
                title = getString(R.string.app_name),
                body = getString(R.string.two_factor_successfully))
        }
    }

    override fun onResume() {
        super.onResume()
        completeData()
    }

    private fun openBiometricDialog(){

        if (!AppPreferences.hasBiometric()){

            AppDialog.showDialog(this@UserProfileActivity,
                title = getString(R.string.app_name),
                body = getString(R.string.want_to_add_biometric),
                confirm = getString(R.string.ok),
                cancel = getString(R.string._cancel),
                cancelListener = object : AppDialog.CancelListener{
                    override fun onCancel() {
                        binding.mSwitchFaceId.isChecked = false
                    }
                },
                confirmListener = object : AppDialog.ConfirmListener{
                    override fun onClick() {
                        showBiometricDialog({
                            AppPreferences.setBiometric(true)
                            binding.mSwitchFaceId.isChecked = true

                            AppDialog.showDialog(this@UserProfileActivity,
                                title = getString(R.string.app_name),
                                body = getString(R.string.biometric_successfully))

                        }, { binding.mSwitchFaceId.isChecked = false })
                    }
                }
            )
        } else {

            AppDialog.showDialog(this@UserProfileActivity,
                title = getString(R.string.app_name),
                body = getString(R.string.want_to_remove_biometric),
                confirm = getString(R.string.ok),
                cancel = getString(R.string._cancel),
                cancelListener = object : AppDialog.CancelListener{
                    override fun onCancel() {
                        binding.mSwitchFaceId.isChecked = true
                    }
                },
                confirmListener = object : AppDialog.ConfirmListener{
                    override fun onClick() {
                        showBiometricDialog({
                            AppPreferences.setBiometric(false)
                            binding.mSwitchFaceId.isChecked = false
                        }, { binding.mSwitchFaceId.isChecked = true })
                    }
                }
            )
        }
    }

    private fun completeData() {
        val user = AppPreferences.getUser()
        binding.tvUsername.text = user?.fullname
        binding.mEmail.text = user?.email
        binding.mPhoto.clipToOutline = true
        Glide.with(this).load(user?.profilePicture).placeholder(R.drawable.placeholder_user_profile).circleCrop().into(binding.mPhoto)
    }

    private fun delete() {
        AppDialog.showDialog(this,
            title = getString(R.string.app_name),
            body = getString(R.string.delete_description),
            confirm = getString(R.string._yes),
            cancel = getString(R.string._no),
            confirmListener = object : AppDialog.ConfirmListener{
                override fun onClick() {
                    showProgressDialog()
                    viewModel.deleteAccount()
                }
            }
        )
    }

    private fun goOut(){
        hideProgressDialog()
        LoginManager.getInstance().logOut()
        AppPreferences.clearData()
        startActivity(
            Intent(this@UserProfileActivity, LoginActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    private fun logout() {
        AppDialog.showDialog(this@UserProfileActivity,
            title = getString(R.string.app_name),
            body = getString(R.string.logout_description),
            confirm = getString(R.string._yes),
            cancel = getString(R.string._no),
            confirmListener = object : AppDialog.ConfirmListener{
                override fun onClick() {
                    viewModel.logout()
                    goOut()
                }
            }
        )
    }
}