package com.infinix.instalane.data.local

import android.content.Context
import com.google.gson.Gson
import com.infinix.instalane.InstalaneApplication
import com.infinix.instalane.data.remote.response.User

object AppPreferences {
    private const val PREFS_NAME = "INSTALANE_PREFENCES"

    private const val USER = "USER"
    private const val DEVICE_TOKEN = "DEVICE_TOKEN"

    private const val SERVER_CLIENT_ID = "SERVER_CLIENT_ID"
    private const val CLIENT_SECRET = "CLIENT_SECRET"
    private const val GOOGLE_PLACE_SERVER_KEY = "GOOGLE_PLACE_SERVER_KEY"

    private const val SHOW_TUTORIAL = "SHOW_TUTORIAL"

    private fun getPreferences() =
        InstalaneApplication.instance.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getUser(): User? {
        val json = getPreferences().getString(USER, "")
        return Gson().fromJson(json, User::class.java)
    }

    fun setUser(user: User) = getPreferences().edit()?.putString(USER, Gson().toJson(user))?.apply()

    fun setDeviceToken(deviceToken:String){
        getPreferences().edit()?.putString(DEVICE_TOKEN, deviceToken)?.apply()
    }

    fun getDeviceToken():String? {
        return getPreferences().getString(DEVICE_TOKEN, "")
    }

    fun setShowTutorial(showTutorial:Boolean){ getPreferences().edit()?.putBoolean(SHOW_TUTORIAL, showTutorial)?.apply() }

    fun mustShowTutorial():Boolean {return getPreferences().getBoolean(SHOW_TUTORIAL, true)}

    fun setServerClientId(key:String){ getPreferences().edit()?.putString(SERVER_CLIENT_ID, key)?.apply() }

    fun getServerClientId():String? { return getPreferences().getString(SERVER_CLIENT_ID, "") }

    fun setClientSecret(key:String){ getPreferences().edit()?.putString(CLIENT_SECRET, key)?.apply() }

    fun getClientSecret():String? { return getPreferences().getString(CLIENT_SECRET, "") }

    fun setGoogleServerPlaceKey(key: String) { getPreferences().edit()?.putString(GOOGLE_PLACE_SERVER_KEY, key)?.apply() }

    fun getGoogleServerPlaceKey(): String { return getPreferences().getString(GOOGLE_PLACE_SERVER_KEY, "")?:"" }

    fun clearData() = getPreferences().edit()?.clear()?.apply()
}