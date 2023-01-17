package com.infinix.instalane.ui.start

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.facebook.login.LoginManager
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.databinding.ActivitySplashBinding
import com.infinix.instalane.ui.base.ActivityAppBase
import com.infinix.instalane.ui.home.MainActivity
import com.infinix.instalane.ui.home.profile.UserProfileViewModel
import com.infinix.instalane.ui.homeGuard.MainGuardActivity
import com.infinix.instalane.ui.start.login.LoginActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : ActivityAppBase() {

    private val viewModel by lazy {
        ViewModelProvider(this)[UserProfileViewModel::class.java].apply {
            logoutLiveData.observe(this@SplashActivity) {}
        }
    }

    private val binding by lazy { ActivitySplashBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (!AppPreferences.isRememberMe()) {
            viewModel.logout()
            LoginManager.getInstance().logOut()
            AppPreferences.clearData()
        }

        Handler(Looper.myLooper()!!).postDelayed({
            if (AppPreferences.getUser()==null){
                startActivity(Intent(this,LoginActivity::class.java))
                finish()
            }
            else {
                if (AppPreferences.hasBiometric())
                    showBiometricDialog( { goToMain() }, { if (!it) finish() } )
                else
                    goToMain()
            }

        }, 2000)
    }

    private fun goToMain(){
        if (AppPreferences.getUser()!!.isUser())
            startActivity(Intent(this, MainActivity::class.java))
        else
            startActivity(Intent(this, MainGuardActivity::class.java))
        finish()
    }
}