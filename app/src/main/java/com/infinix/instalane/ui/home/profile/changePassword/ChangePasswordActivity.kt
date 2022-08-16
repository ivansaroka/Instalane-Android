package com.infinix.instalane.ui.home.profile.changePassword

import android.os.Bundle
import com.infinix.instalane.R
import com.infinix.instalane.databinding.ActivityChangePasswordBinding
import com.infinix.instalane.ui.base.ActivityAppBase

class ChangePasswordActivity : ActivityAppBase() {

    private val binding by lazy { ActivityChangePasswordBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setToolbar(getString(R.string.change_password))
    }
}