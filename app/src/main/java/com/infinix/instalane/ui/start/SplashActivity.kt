package com.infinix.instalane.ui.start

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.databinding.ActivitySplashBinding
import com.infinix.instalane.ui.base.ActivityAppBase
import com.infinix.instalane.ui.home.MainActivity
import com.infinix.instalane.ui.homeGuard.MainGuardActivity
import com.infinix.instalane.ui.start.login.LoginActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : ActivityAppBase() {

    private val binding by lazy { ActivitySplashBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

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