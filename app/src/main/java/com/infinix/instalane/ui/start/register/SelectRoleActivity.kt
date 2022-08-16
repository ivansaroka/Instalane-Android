package com.infinix.instalane.ui.start.register

import android.os.Bundle
import com.infinix.instalane.data.remote.response.User
import com.infinix.instalane.databinding.ActivitySelectRoleBinding
import com.infinix.instalane.ui.base.ActivityAppBase

class SelectRoleActivity : ActivityAppBase() {

    private val binding by lazy { ActivitySelectRoleBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.mClose.setOnClickListener { finish() }

        binding.mUserRole.setOnClickListener {
            startActivity(RegisterActivity.getIntent(this, User.ROLE_USER))
            finish()
        }

        binding.mGuardRole.setOnClickListener {
            startActivity(RegisterActivity.getIntent(this, User.ROLE_GUARD))
            finish()
        }
    }
}