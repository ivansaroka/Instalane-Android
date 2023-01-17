package com.infinix.instalane.utils
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.text.format.DateUtils
import com.infinix.instalane.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue


class DateUtils {

    companion object {
        const val DATE_TIME_FORMAT_DEFAULT = "MM-dd-yyyy hh:mm:ss"
        const val DATE_YEAR = "MMMM dd, yyyy"
        const val DATE_EVENT = "MM-dd-yy hh:mm aa"
        const val DATE_DEFAULT_API = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        const val HOUR_MESSAGE = "hh:mm aa"
        const val DATE_MESSAGE = "EEE MMM dd"

        const val FORMAT_DAY = "EEEE"
        const val FORMAT_CHAT_DEFAULT = "MM-dd-yyyy"
        const val FORMAT_HOME_FILE = "MMMM yyyy"

        const val FORMAT_ORDER_DATE = "MMM dd, yyyy"
        const val FORMAT_HOUR = "hh:mm aa"

        const val HOUR_SCHEDULE = "hh:mm"
        const val DATE_SCHEDULE = "yyyy-MM-dd"
        const val DATE_SCHEDULE_COMPLETE = "yyyy-MM-dd hh:mm aa"

        const val FORMAT_MESSAGE_WEEK = "EEEE"
        const val FORMAT_MESSAGE_OLD = "MMM dd, yyyy"

        const val FORMAT_EXPIRED_API = "MM-dd-yyyy"
        const val FORMAT_EXPIRED = "MMM dd, yyyy"
        const val FORMAT_SHOPPING = "MMMM dd, yyyy"

        const val FORMAT_NOTIFICATION_API = "MM-dd-yyyy HH:mm:ss"
        const val FORMAT_ORDER_API = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        const val FORMAT_NOTIFICATION_APP = "MM-dd-yy hh:mm aa"

        const val FORMAT_ORDER_DATE_COMPLETE = "HH:mm:ss MM/dd/yyyy"
        const val FORMAT_LAST_ACTIVITY = "MM/dd/yyyy"
    }


    fun convertDate(
        dateToConvert: String,
        dateFormatIn: String,
        dateFormatOut: String,
        dateInIsUTC: Boolean = false
    ): String {

        try {
            val dateFormat = SimpleDateFormat(dateFormatIn)
            if (dateInIsUTC)
                dateFormat.timeZone = TimeZone.getTimeZone("UTC")

            var convertedDate = dateFormat.parse(dateToConvert)
            val convertCalendar = Calendar.getInstance()
            convertCalendar.time = convertedDate
            convertedDate = convertCalendar.time

            val output = SimpleDateFormat(dateFormatOut)
            return output.format(convertedDate).capitalize()

        } catch (e: Exception) {
            return ""
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun convertFromStringToDate(dateToConvert: String, dateFormatIn: String): Date? {

        try {
            val dateFormat = SimpleDateFormat(dateFormatIn, Locale.ENGLISH)
            var convertedDate = dateFormat.parse(dateToConvert)
            val convertCalendar = Calendar.getInstance()
            convertCalendar.time = convertedDate
            convertedDate = convertCalendar.time
            return convertedDate

        } catch (e: Exception) {
            return null
        }
    }

    fun convertFromDateToString(date: Date, dateFormatOut: String): String {
        val output = SimpleDateFormat(dateFormatOut)
        var result = output.format(date).capitalize()

        if (result.startsWith("0"))
            result = result.substring(1, result.length)
        return result
    }

    fun convertFromDateToStringUTC(date: Date, dateFormatOut: String): String {
        val output = SimpleDateFormat(dateFormatOut)
        output.timeZone = TimeZone.getTimeZone("UTC")
        return output.format(date).capitalize()
    }


    fun getDayNumberSuffix(day: Int): String {
        if (day in 11..13) {
            return "th"
        }
        return when (day % 10) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    }


    fun openDatePicker(
        context: Context,
        currentCalendar: Calendar,
        showYear: Boolean = false,
        onDateSelected: (date: Date) -> Unit
    ) {
        val year = currentCalendar.get(Calendar.YEAR)
        val month = currentCalendar.get(Calendar.MONTH)
        val day = currentCalendar.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(
            context,
            R.style.datePickerTheme,
            { _, year, monthOfYear, dayOfMonth ->
                val calendarSelected = Calendar.getInstance()
                calendarSelected.set(Calendar.YEAR, year)
                calendarSelected.set(Calendar.MONTH, monthOfYear)
                calendarSelected.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                onDateSelected(calendarSelected.time)
            },
            year,
            month,
            day
        )
        dialog.datePicker.minDate = currentCalendar.timeInMillis
        dialog.show()

        if (showYear) dialog.datePicker.touchables[0].performClick()
    }

    fun openTimePicker(
        context: Context,
        currentCalendar: Calendar,
        onTimeSelected: (date: Date) -> Unit
    ) {

        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)

            onTimeSelected(cal.time)

        }
        TimePickerDialog(
            context,
            timeSetListener,
            currentCalendar.get(Calendar.HOUR_OF_DAY),
            currentCalendar.get(Calendar.MINUTE),
            false
        ).show()
    }

    fun conversationDate(date: Int): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = date * 1000L

        val today = Calendar.getInstance()

        if (today.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR) &&
            today.get(Calendar.MONTH) == cal.get(Calendar.MONTH) &&
            today.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
        )
            return convertFromDateToString(cal.time, FORMAT_HOUR)
        else
            return convertFromDateToString(cal.time, FORMAT_CHAT_DEFAULT)
    }

    fun messageDate(date: Int): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = date * 1000L

        val today = Calendar.getInstance()

        if (today.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR) &&
            today.get(Calendar.MONTH) == cal.get(Calendar.MONTH) &&
            today.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
        ) {

            if ((cal.timeInMillis - today.timeInMillis).absoluteValue < 5000)
                return "Now"
            else
                return convertFromDateToString(cal.time, FORMAT_HOUR)
        } else
            return convertFromDateToString(cal.time, FORMAT_CHAT_DEFAULT)

    }

    fun calculateDateDifference(date: Date): String {

        try {
             val date2: Date = Calendar.getInstance().time
             val diff = date2.time - date.time
             val days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt()

             return when (days) {
                 0 -> if (DateUtils.isToday(date.time)) convertFromDateToString(date, FORMAT_HOUR) else "Yesterday"
                 1 -> "Yesterday"
                 in 2..6 -> convertFromDateToString(date, FORMAT_MESSAGE_WEEK)
                 else -> convertFromDateToString(date, FORMAT_MESSAGE_OLD)
             }

        } catch (e: Exception) {
        }

        return ""
    }

    fun generateEventDateTime(
        context: Context, currentCalendar: Calendar,
        onDateSelected: (date: Date, sDateFormat: String) -> Unit
    ) {

        openDatePicker(context, currentCalendar) { date ->

            openTimePicker(context, currentCalendar) { time ->

                val calTime = Calendar.getInstance()
                calTime.time = time

                val calDate = Calendar.getInstance()
                calDate.time = date
                calDate.set(Calendar.HOUR_OF_DAY, calTime.get(Calendar.HOUR_OF_DAY))
                calDate.set(Calendar.MINUTE, calTime.get(Calendar.MINUTE))

                val sEventDate = convertFromDateToString(calDate.time, DATE_EVENT)
                onDateSelected(calDate.time, sEventDate)

            }
        }
    }

    fun fromMonthTextToNumber(month: String): Int {
        return when (month.toUpperCase()) {
            "JAN" -> 0
            "FEB" -> 1
            "MAR" -> 2
            "APR" -> 3
            "MAY" -> 4
            "JUN" -> 5
            "JUL" -> 6
            "AUG" -> 7
            "SEP" -> 8
            "OCT" -> 9
            "NOV" -> 10
            "DEC" -> 11
            else -> 0
        }
    }
}
