package com.infinix.instalane.ui.homeGuard.profile

import android.content.Intent
import android.os.Bundle
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.infinix.instalane.R
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.databinding.ActivityGuardProfileBinding
import com.infinix.instalane.ui.base.ActivityAppBase
import com.infinix.instalane.ui.home.profile.changePassword.ChangePasswordActivity
import com.infinix.instalane.ui.home.profile.editProfile.EditProfileActivity
import com.infinix.instalane.ui.start.login.LoginActivity
import com.infinix.instalane.utils.AppDialog

class GuardProfileActivity : ActivityAppBase() {

    private val binding by lazy { ActivityGuardProfileBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setToolbar()

        binding.mEdit.setOnClickListener { startActivity(Intent(this, EditProfileActivity::class.java)) }
        binding.mContPassword.setOnClickListener { startActivity(Intent(this, ChangePasswordActivity::class.java)) }
        binding.mLogout.setOnClickListener { logout() }
    }

    override fun onResume() {
        super.onResume()
        completeData()
    }

    private fun completeData() {
        val user = AppPreferences.getUser()
        binding.tvUsername.text = user?.fullname
        binding.mEmail.text = user?.email
        binding.mPhoto.clipToOutline = true
        Glide.with(this).load(user?.profilePicture).placeholder(R.drawable.placeholder_user_profile).circleCrop().into(binding.mPhoto)
    }

    private fun logout() {
        AppDialog.showDialog(this@GuardProfileActivity,
            title = getString(R.string.app_name),
            body = getString(R.string.logout_description),
            confirm = getString(R.string.ok),
            cancel = getString(R.string._cancel),
            confirmListener = object : AppDialog.ConfirmListener{
                override fun onClick() {
                    LoginManager.getInstance().logOut()
                    AppPreferences.clearData()
                    startActivity(
                        Intent(this@GuardProfileActivity, LoginActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
                }
            }
        )
    }
}