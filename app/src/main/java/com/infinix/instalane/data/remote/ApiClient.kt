package com.infinix.instalane.data.remote

import android.util.Log
import com.infinix.instalane.BuildConfig
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.concurrent.TimeUnit
import kotlin.reflect.KSuspendFunction1
import kotlin.reflect.KSuspendFunction0
import kotlin.reflect.KSuspendFunction2
import kotlin.reflect.KSuspendFunction3
import kotlin.reflect.KSuspendFunction4
import kotlin.reflect.KSuspendFunction5

object ApiClient {
    private const val BASE_DEV_URL = "https://instalane.xanthops.com/api/v1/"
    private const val BASE_PROD_URL = "https://admin.instalaneusa.com/api/v1/"

    val service = createService()

    private fun createService(): ApiInterface {
        val httpClient = getOkHttpClient()

        val sRestAdapter = Retrofit.Builder()
            .baseUrl(BASE_DEV_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()

        return sRestAdapter.create(ApiInterface::class.java)
    }

    private fun getOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG)
            builder.addInterceptor(HttpLoggingInterceptor { message ->
                Log.d("ApiClient", message)
            }.apply { level = HttpLoggingInterceptor.Level.BODY })

        val timeout = 30L
        return builder
            .connectTimeout(timeout, TimeUnit.SECONDS)
            .readTimeout(timeout, TimeUnit.SECONDS)
            .writeTimeout(timeout, TimeUnit.SECONDS)
            .build()
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


    fun <ID, REQUEST, RESPONSE> KSuspendFunction2<ID, REQUEST, RESPONSE>.callApi(id: ID, request: REQUEST) =
        flow {
            try {
                val response = this@callApi.invoke(id,request)
                emit(Result.success(response))
            } catch (e: Exception) {
                emit(Result.failure(e))
            }
        }

    fun <PARM1, PARM2, PARM3, RESPONSE> KSuspendFunction3<PARM1, PARM2, PARM3, RESPONSE>
            .callApi(parm1: PARM1,parm2: PARM2, parm3: PARM3) =
        flow {
            try {
                val response = this@callApi.invoke(parm1,parm2,parm3)
                emit(Result.success(response))
            } catch (e: Exception) {
                emit(Result.failure(e))
            }
        }

    fun <PARM1, PARM2, PARM3, PARM4, RESPONSE> KSuspendFunction4<PARM1, PARM2, PARM3, PARM4, RESPONSE>
            .callApi(parm1: PARM1,parm2: PARM2, parm3: PARM3, parm4: PARM4) =
        flow {
            try {
                val response = this@callApi.invoke(parm1,parm2,parm3,parm4)
                emit(Result.success(response))
            } catch (e: Exception) {
                emit(Result.failure(e))
            }
        }

    fun <PARM1, PARM2, PARM3, PARM4, PARM5, RESPONSE> KSuspendFunction5<PARM1, PARM2, PARM3, PARM4, PARM5, RESPONSE>
            .callApi(parm1: PARM1,parm2: PARM2, parm3: PARM3, parm4: PARM4,parm5: PARM5) =
        flow {
            try {
                val response = this@callApi.invoke(parm1,parm2,parm3,parm4,parm5)
                emit(Result.success(response))
            } catch (e: Exception) {
                emit(Result.failure(e))
            }
        }

    fun <RESPONSE> KSuspendFunction0<RESPONSE>.callApi() =
        flow {
            try {
                val response = this@callApi.invoke()
                emit(Result.success(response))
            } catch (e: Exception) {
                emit(Result.failure(e))
            }
        }
}