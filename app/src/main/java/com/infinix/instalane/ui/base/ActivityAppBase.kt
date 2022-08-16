package com.infinix.instalane.ui.base

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.infinix.instalane.R
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.utils.LoaderDialog
import com.infinix.instalane.utils.showErrorMessage

open class ActivityAppBase : AppCompatActivity() {

    private var mDialog: LoaderDialog? = null
    lateinit var mGoogleSignInClient: GoogleSignInClient
    var onGoogleAuthSuccess: ((GoogleSignInAccount, String?) -> Unit?)? = null

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



}