package com.infinix.instalane.ui.start.login

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.infinix.instalane.data.local.AppPreferences
import com.infinix.instalane.data.remote.ApiClient
import com.infinix.instalane.data.remote.AppGoogleService
import com.infinix.instalane.data.remote.AppGoogleService.callApi
import com.infinix.instalane.data.remote.request.*
import com.infinix.instalane.utils.BaseViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class LoginViewModel(application: Application) : BaseViewModel(application) {

    val loginLiveData = MutableLiveData<Any>()
    val onErrorLogin = MutableLiveData<Throwable?>()
    val loginSocialNetLiveData = MutableLiveData<Any>()
    val forgetPasswordLiveData = MutableLiveData<Any>()

    fun login(email: String, password: String) =
        viewModelScope.launch {

            ApiClient.service::login.callApi(LoginRequest(email, password)).collect {
                if (it.isSuccess) {
                    it.getOrNull()?.let { user -> AppPreferences.setUser(user) }
                    loginLiveData.postValue(it)
                } else
                    onErrorLogin.postValue(it.exceptionOrNull())
            }

        }

    fun forgotPassword(email:String) {
        viewModelScope.launch {
            ApiClient.service::forgotPassword.callApi(ForgotPasswordRequest(email)).collect {
                if (it.isSuccess)
                    forgetPasswordLiveData.postValue(it)
                else
                    onError.postValue(it.exceptionOrNull())
            }
        }
    }

    fun loginWithGoogle(account: GoogleSignInAccount){

        viewModelScope.launch {
            val requestGoogle = OauthRequest().apply {
                this.code = account.serverAuthCode!!
                this.idToken = account.idToken!!
            }
            AppGoogleService.getClientOauth()!!::getAccessToken.callApi(requestGoogle).collect {
                if (it.isSuccess){
                    it.getOrNull()?.let {

                        val request = RegisterGoogleRequest(
                            googleId = account.id,
                            googleToken = it.accessToken!!
                        )
                        ApiClient.service::registerWithGoogle.callApi(request).collect { result->
                            if (result.isSuccess){
                                result.getOrNull()?.let { user ->
                                    AppPreferences.setUser(user)
                                    loginSocialNetLiveData.postValue("")
                                }
                            } else
                                onError.postValue(result.exceptionOrNull())
                        }
                    }
                } else
                    onError.postValue(it.exceptionOrNull())
            }
        }
    }

    fun loginWithFacebook(facebookId:String, facebookToken:String){
        viewModelScope.launch {
            val request = RegisterFacebookRequest().apply {
                this.fbId = facebookId
                this.fbToken = facebookToken
            }

            ApiClient.service::registerWithFacebook.callApi(request).collect { result->
                if (result.isSuccess) {
                    result.getOrNull()?.let { user ->
                        AppPreferences.setUser(user)
                        loginSocialNetLiveData.postValue("")
                    }
                } else
                    onError.postValue(result.exceptionOrNull())
            }
        }
    }
}