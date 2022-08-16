package com.infinix.instalane.data.remote

import com.infinix.instalane.data.remote.request.OauthRequest
import com.infinix.instalane.data.remote.response.OauthResponse
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit
import kotlin.reflect.KSuspendFunction1

object AppGoogleService {

    private val API_URL_OAUTH = "https://www.googleapis.com/oauth2/v4/"

    private var sApiClientOauth: ApiClientInterfaceOauth? = null
    private var sRestAdapterOauth: Retrofit? = null

    private val httpClient = OkHttpClient.Builder()
    var cliente: OkHttpClient? = null

    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        httpClient.readTimeout(2, TimeUnit.MINUTES)
        httpClient.connectTimeout(2, TimeUnit.MINUTES)
        httpClient.addInterceptor(interceptor)
        cliente = httpClient.build()
    }

    fun getClientOauth(): ApiClientInterfaceOauth? {
        if (sApiClientOauth == null)
            initApiClientOauth()
        return sApiClientOauth
    }


    private fun initApiClientOauth() {
        sRestAdapterOauth = Retrofit.Builder()
            .baseUrl(API_URL_OAUTH)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        sApiClientOauth = sRestAdapterOauth!!.create(ApiClientInterfaceOauth::class.java)
    }

    interface ApiClientInterfaceOauth {
        @POST("token")
        suspend fun getAccessToken(@Body calendarID: OauthRequest): OauthResponse
    }


    fun <REQUEST, RESPONSE> KSuspendFunction1<REQUEST, RESPONSE>.callApi(request: REQUEST) =
        flow {
            try {
                val response = this@callApi.invoke(request)
                emit(Result.success(response))
            } catch (e: Exception) {
                emit(Result.failure(e))
            }
        }
}