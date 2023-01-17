package com.infinix.instalane.ui.homeGuard.profile

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
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
import com.infinix.instalane.utils.DateUtils
import java.util.*

class GuardProfileActivity : ActivityAppBase() {

    private val viewModel by lazy {
        ViewModelProvider(this)[GuardProfileViewModel::class.java].apply {
            profileLiveData.observe(this@GuardProfileActivity) { completeData() }
        }
    }


    private val binding by lazy { ActivityGuardProfileBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setToolbar()

        binding.mEdit.setOnClickListener { startActivity(Intent(this, EditProfileActivity::class.java)) }
        binding.mContPassword.setOnClickListener { startActivity(Intent(this, ChangePasswordActivity::class.java)) }
        binding.mLogout.setOnClickListener { logout() }
        viewModel.getProfile()
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

        val dateUtils = DateUtils()
        binding.mLastActivity.text = ""
        if (user?.lastActivity != null) {
            val sDate = dateUtils.convertDate(user.lastActivity!!, DateUtils.DATE_DEFAULT_API, DateUtils.FORMAT_LAST_ACTIVITY)
            binding.mLastActivity.text = sDate
        }

        binding.mLoginTime.text = ""
        if (user?.loginTime != null) {

            val date = DateUtils().convertFromStringToDate(user.loginTime!!, DateUtils.DATE_DEFAULT_API)
            if (date != null) {
                val timeZone: String = Calendar.getInstance().timeZone.id
                val local = Date(date.time + TimeZone.getTimeZone(timeZone).getOffset(date.time))
                val sDate =  DateUtils().convertFromDateToString(local, DateUtils.FORMAT_HOUR)
                binding.mLoginTime.text = sDate.replace("a. m.", "AM").replace("p. m.", "PM")
            }
        }

        binding.mLogoutTime.text = ""
        if (user?.logoutTime != null) {

            val date = DateUtils().convertFromStringToDate(user.logoutTime!!, DateUtils.DATE_DEFAULT_API)
            if (date != null) {
                val timeZone: String = Calendar.getInstance().timeZone.id
                val local = Date(date.time + TimeZone.getTimeZone(timeZone).getOffset(date.time))
                val sDate =  DateUtils().convertFromDateToString(local, DateUtils.FORMAT_HOUR)
                binding.mLogoutTime.text = sDate.replace("a. m.", "AM").replace("p. m.", "PM")
            }
        }

        binding.mRegionName.text =user!!.region?.name
        binding.mStoreName.text =user.store?.name
        binding.mCompanyName.text = user.region?.company?.name
        binding.mEmployeeID.text = user.employeeId
        //binding.mStoreName.text = user.region.company.s
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