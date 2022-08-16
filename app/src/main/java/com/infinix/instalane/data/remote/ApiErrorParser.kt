package com.infinix.instalane.data.remote

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.infinix.instalane.R
import com.infinix.instalane.data.remote.response.DefaultResponse
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ApiErrorParser {

    fun getErrorResponse(error: Throwable?): DefaultResponse? {
        var responseError: DefaultResponse? = null
        try {
            if (error is HttpException)
                responseError = Gson().fromJson(
                    error.response()?.errorBody()!!.string(),
                    DefaultResponse::class.java
                )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return responseError
    }

    fun parseError(context: Context, error: Throwable?): String {
        return when (error) {
            is HttpException -> {
                try {
                    when (error.code()) {
                        in 400..499 -> {
                            val response = Gson().fromJson(
                                error.response()?.errorBody()!!.string(),
                                DefaultResponse::class.java
                            )

                            return response.message ?: context.getString(R.string.error_default)
                        }
                        // in 500..599 -> context.getString(R.string.server_error)
                        else -> "Unknown Error"
                    }
                } catch (e: Exception) {
                    return context.getString(R.string.error_default)
                }
            }
            is SocketTimeoutException -> context.getString(R.string.time_out)
            is UnknownHostException -> context.getString(R.string.no_internet)
            is JsonParseException -> context.getString(R.string.error_default)
            is IOException -> context.getString(R.string.error_default)
            is Throwable ->  return error.message ?: context.getString(R.string.error_default)
            else -> context.getString(R.string.error_default)
        }
    }
}