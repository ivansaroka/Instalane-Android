package com.infinix.instalane.data.remote.response

import com.google.gson.annotations.SerializedName
import com.infinix.instalane.utils.DateUtils
import java.util.*

class Notification {

    @SerializedName("id")
    var id: String? = null

    @SerializedName("coupon")
    var coupon: Coupon? = null

    @SerializedName("title")
    var title: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("created_datetime")
    var date: String? = null

    fun convertToDate() : Date? {
        val date = DateUtils().convertFromStringToDate(date!!, DateUtils.FORMAT_NOTIFICATION_API)
        if (date != null) {
            val timeZone: String = Calendar.getInstance().timeZone.id
            return Date(date.time + TimeZone.getTimeZone(timeZone).getOffset(date.time))
        }
        return null
    }

}