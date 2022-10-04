package com.infinix.instalane.ui.home.profile.editProfile

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.infinix.instalane.R
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.databinding.ActivityEditProfileBinding
import com.infinix.instalane.ui.base.ActivityAppBasePhotoProfile
import com.infinix.instalane.ui.home.profile.UserProfileViewModel
import com.infinix.instalane.utils.AppDialog
import com.infinix.instalane.utils.ChoosePhotoDialog
import com.infinix.instalane.utils.isValidEmail
import com.infinix.instalane.utils.showErrorMessage


class EditProfileActivity : ActivityAppBasePhotoProfile() {

    private val viewModel by lazy {
        ViewModelProvider(this)[UserProfileViewModel::class.java].apply {
            editLiveData.observe(this@EditProfileActivity) {
                hideProgressDialog()
                AppDialog.showDialog(this@EditProfileActivity,
                    title = getString(R.string.app_name),
                    body = getString(R.string.successfully_edited),
                    confirm = getString(R.string.ok),
                    confirmListener = object : AppDialog.ConfirmListener{
                        override fun onClick() {
                            finish()
                        }
                    }
                )
            }
            onError.observe(this@EditProfileActivity) {
                hideProgressDialog()
                showErrorMessage(it)
            }
        }
    }

    private val binding by lazy { ActivityEditProfileBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setToolbar()
        binding.mPhoto.clipToOutline = true
        completeData()

        binding.mPhoto.setOnClickListener {
            val dialog = ChoosePhotoDialog()
            dialog.onCamera = { openCamera { binding.mPhoto.setImageBitmap(it) } }
            dialog.onGallery = { openGallery { binding.mPhoto.setImageBitmap(it) } }
            dialog.show(supportFragmentManager, "")
        }
        binding.mConfirm.setOnClickListener { edit() }
    }

    private fun edit() {
        binding.etFullName.error = null

        val fullName = binding.etFullName.text.toString()
        val email = binding.etEmail.text.toString()
        val phoneNumber = binding.etPhoneNumber.text.toString()

        when {
            fullName.isBlank() -> binding.etFullName.error = getString(R.string.field_required)
            email.isBlank() -> binding.etEmail.error = getString(R.string.field_required)
            !isValidEmail(email) -> binding.etEmail.error = getString(R.string.invalid_email)
            phoneNumber.isBlank() -> binding.etPhoneNumber.error = getString(R.string.field_required)
            else ->{
                showProgressDialog()
                viewModel.editUser(fullName, email, phoneNumber, fileSelected)
            }
        }

    }

    private fun completeData() {
        hideProgressDialog()
        val user = AppPreferences.getUser()
        binding.etFullName.setText(user?.fullname)
        binding.etEmail.setText(user?.email)
        binding.etPhoneNumber.setText(user?.mobileNumber)
        Glide.with(this)
            .load(user?.profilePicture)
            .placeholder(R.drawable.placeholder_user_profile)
            .apply(RequestOptions.circleCropTransform())
            .into(binding.mPhoto)
    }

}