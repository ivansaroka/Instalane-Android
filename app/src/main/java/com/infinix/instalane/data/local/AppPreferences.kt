package com.infinix.instalane.data.local

import android.content.Context
import com.google.gson.Gson
import com.infinix.instalane.InstalaneApplication
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.Product
import com.infinix.instalane.data.remote.response.Store
import com.infinix.instalane.data.remote.response.User

object AppPreferences {
    private const val PREFS_NAME = "INSTALANE_PREFENCES"

    private const val USER = "USER"
    private const val DEVICE_TOKEN = "DEVICE_TOKEN"

    private const val SERVER_CLIENT_ID = "SERVER_CLIENT_ID"
    private const val CLIENT_SECRET = "CLIENT_SECRET"
    private const val GOOGLE_PLACE_SERVER_KEY = "GOOGLE_PLACE_SERVER_KEY"

    private const val SHOW_TUTORIAL = "SHOW_TUTORIAL"
    private const val HAS_BIOMETRIC = "HAS_BIOMETRIC"

    private const val DRAFT_PRODUCTS = "DRAFT_PRODUCTS"

    private fun getPreferences() =
        InstalaneApplication.instance.getSharedPreferences(InstalaneApplication.appContext.getString(R.string.app_preference), Context.MODE_PRIVATE)

    fun getUser(): User? {
        val json = getPreferences().getString(USER, "")
        return Gson().fromJson(json, User::class.java)
    }

    fun setUser(user: User) = getPreferences().edit()?.putString(USER, Gson().toJson(user))?.apply()


    private fun setDraftProduct(draftProducts: DraftProducts?) = getPreferences().edit()?.putString(DRAFT_PRODUCTS, Gson().toJson(draftProducts))?.apply()

    private fun getDraftProduct(): DraftProducts? {
        val json = getPreferences().getString(DRAFT_PRODUCTS, "")
        return Gson().fromJson(json, DraftProducts::class.java)
    }

    fun getDraftProductsByStore(store: Store) : ArrayList<Product>?{
        val draft = getDraftProduct()
        if (draft!=null){
            if (draft.store?.id == store.id) return draft.productList
        }
        return null
    }

    fun addToDraft(store: Store, product: Product){
        var draft = getDraftProduct()
        if (draft==null) draft = DraftProducts().apply {
            this.store = store
            this.productList = ArrayList()
        }

        if (draft.store?.id != store.id)
            draft.productList?.clear()
        draft.productList!!.add(product)

        setDraftProduct(draft)
    }

    fun removeFromDraft(store: Store, product: Product){
        var draft = getDraftProduct()
        if (draft==null) draft = DraftProducts().apply {
            this.store = store
            this.productList = ArrayList()
        }
        draft.productList?.removeIf { it.id == product.id }
        setDraftProduct(draft)
    }

    fun cleanDraft(store: Store) {
        val draft = getDraftProduct()
        if (draft!=null && draft.store?.id == store.id)
            setDraftProduct(null)
    }

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

    fun clearData() {
        val tutorial = mustShowTutorial()
        val biometric = hasBiometric()

        getPreferences().edit()?.clear()?.apply()

        setShowTutorial(tutorial)
        setBiometric(biometric)
    }

    fun hasBiometric(): Boolean = getPreferences().getBoolean(HAS_BIOMETRIC, false)

    fun setBiometric(hasBiometric: Boolean) = getPreferences().edit()?.putBoolean(HAS_BIOMETRIC, hasBiometric)?.apply()
}