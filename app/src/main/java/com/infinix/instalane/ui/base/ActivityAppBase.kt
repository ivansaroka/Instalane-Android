package com.infinix.instalane.ui.base

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.infinix.instalane.InstalaneApplication
import com.infinix.instalane.R
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.data.remote.ApiErrorParser
import com.infinix.instalane.utils.AppDialog
import com.infinix.instalane.utils.LoaderDialog
import com.infinix.instalane.utils.showErrorMessage
import com.infinix.instalane.utils.showMessage

open class ActivityAppBase : AppCompatActivity() {

    private var mDialog: LoaderDialog? = null
    lateinit var mGoogleSignInClient: GoogleSignInClient
    var onGoogleAuthSuccess: ((GoogleSignInAccount, String?) -> Unit?)? = null
    var isInBackground = false

    override fun onResume() {
        super.onResume()
        val myApp = this.application as InstalaneApplication
        if (myApp.wasInBackground) {
            if (AppPreferences.getUser()!= null && AppPreferences.hasBiometric()){
                showBiometricDialog( {  }, { if (!it) finishAffinity() } )
            }
        }
        myApp.stopActivityTransitionTimer()
    }

    override fun onPause() {
        super.onPause()
        (this.application as InstalaneApplication).startActivityTransitionTimer()
    }


    fun setToolbar(title:String?=null){
        findViewById<View>(R.id.mBack)?.setOnClickListener { finish() }
        findViewById<TextView>(R.id.mTitle)?.text = title
    }

    fun showClose(){
        findViewById<View>(R.id.mBack)?.visibility = View.INVISIBLE
        findViewById<View>(R.id.mClose)?.visibility = View.VISIBLE
        findViewById<View>(R.id.mClose)?.setOnClickListener { finish() }
    }

    fun showProgressDialog() {
        if (!isFinishing) {
            if (mDialog != null && mDialog!!.isShowing)
                hideProgressDialog()
            mDialog = LoaderDialog(this, R.style.NewDialog)
            mDialog?.show()
        }
    }

    fun hideProgressDialog() {
        if (!isFinishing) {
            if (mDialog != null && mDialog!!.isShowing)
                mDialog?.dismiss()
        }
    }

    fun showErrorApi(throwable: Throwable?){
        hideProgressDialog()
        showErrorMessage(throwable)
    }

    protected fun signInWithGoogle(onGoogleAuthSuccess: (GoogleSignInAccount, String?) -> Unit) {
        if (AppPreferences.getServerClientId().isNullOrEmpty()) return
        this.onGoogleAuthSuccess = onGoogleAuthSuccess
        val gso: GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(AppPreferences.getServerClientId()!!)
                .requestServerAuthCode(AppPreferences.getServerClientId()!!)
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = mGoogleSignInClient.signInIntent
        resultGoogle.launch(signInIntent)
    }

    private var resultGoogle = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result!=null && result.resultCode == Activity.RESULT_OK) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Log.w("GOOGLE_ACCOUNT", "email: ${account.email}")
            Log.w("GOOGLE_ACCOUNT", "id: ${account.id}")
            Log.w("GOOGLE_ACCOUNT", "firstName: ${account.givenName}")
            Log.w("GOOGLE_ACCOUNT", "lastName: ${account.familyName}")

            onGoogleAuthSuccess?.invoke(account, account.idToken)
        } catch (e: ApiException) {
            Log.w("GOOGLE_ACCOUNT", "signInResult:failed code=${e.statusCode}")
        }
    }

    protected fun showErrorAlert(text:String){
        AppDialog.showDialog(this,getString(R.string.app_name), text)
    }

    protected fun showErrorAlert(throwable: Throwable?){
        val text = ApiErrorParser.parseErrorInstalane(this, throwable)
        AppDialog.showDialog(this,getString(R.string.app_name), text)
    }

    protected fun showErrorAlertString(throwable: Throwable?){
        val text = ApiErrorParser.parseErrorInstalaneTextError(this, throwable)
        AppDialog.showDialog(this,getString(R.string.app_name), text)
    }

    protected fun showErrorAlertDismiss(text:String){
        AppDialog.showDialog(this,getString(R.string.app_name), text, confirmListener = object : AppDialog.ConfirmListener{
            override fun onClick() {
                finish()
            }
        })
    }

    protected fun showBiometricDialog(onSuccess: () -> Unit, onError:(isFailed:Boolean)->Unit) {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.app_name))
            .setSubtitle(getString(R.string.biometric_description))
            //.setNegativeButtonText(getString(R.string.not_now))
            .setAllowedAuthenticators(BiometricManager.Authenticators.DEVICE_CREDENTIAL or BiometricManager.Authenticators.BIOMETRIC_WEAK)
            //.setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL or BiometricManager.Authenticators.BIOMETRIC_WEAK)
            //.setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()

        val biometricPrompt = BiometricPrompt(this, ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    if (errorCode == BiometricPrompt.ERROR_NO_BIOMETRICS)
                        showErrorAlert("You need to configure biometric unlock in your device")
                    else if (errorCode == BiometricPrompt.ERROR_LOCKOUT){
                        showErrorAlertDismiss("Access to de application has been blocked. You can try again in 30s")
                        onError(true)
                        return
                    } else
                        onError(false)

                    if (errString.isNotEmpty())
                        showMessage("Authentication error: $errString")
                    else
                        showMessage("Authentication error")
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    onSuccess.invoke()
                }

                override fun onAuthenticationFailed() {
                    showMessage("Authentication failed")
                    onError(true)
                }
            })
        biometricPrompt.authenticate(promptInfo)
    }

}