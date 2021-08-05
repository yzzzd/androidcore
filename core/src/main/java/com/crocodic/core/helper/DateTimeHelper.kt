package com.crocodic.core.helper

import com.crocodic.core.extension.encrypt
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class DateTimeHelper(private val currentLocale: Locale = Locale.US) {

    companion object {
        val localeID = Locale("id", "ID")
        val localeUS = Locale.US
    }

    fun secret(iv: String) = "${createAt()}|reprime2021attendance".encrypt(iv)

    fun createAt(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", currentLocale)
        return formatter.format(Date())
    }

    fun createAtLong(): Long {
        return Calendar.getInstance().timeInMillis
    }

    fun monthYear(): String {
        val formatter = SimpleDateFormat("MMMM yyyy", currentLocale)
        return formatter.format(Date())
    }

    fun timeNow(): String {
        val formatter = SimpleDateFormat("HH:mm:ss", currentLocale)
        return formatter.format(Date())
    }

    fun dateNow(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", currentLocale)
        return formatter.format(Date())
    }

    fun datePrettyNow(): String {
        val formatter = SimpleDateFormat("EEE, d MMM yyyy", currentLocale)
        return formatter.format(Date())
    }

    fun periodNow(): String {
        val formatter = SimpleDateFormat("MMMM yyyy", currentLocale)
        return formatter.format(Date())
    }

    fun periodLast(): String {
        val formatter = SimpleDateFormat("MMMM yyyy", currentLocale)

        val now = Calendar.getInstance()
        now.add(Calendar.MONTH, -1)

        return formatter.format(now.time)
    }

    fun periodDateNow(): String {
        val formatter = SimpleDateFormat("EEEE, d MMM yyyy", currentLocale)

        val minD = Calendar.getInstance()
        minD.set(Calendar.DATE, 1)
        val maxD = Calendar.getInstance()
        maxD.set(Calendar.DATE, maxD.getActualMaximum(Calendar.DATE))

        val from = formatter.format(minD.time)
        val to = formatter.format(maxD.time)

        return "$from - $to"
    }

    fun periodDateLast(): String {
        val formatter = SimpleDateFormat("EEEE, d MMM yyyy", currentLocale)

        val minD = Calendar.getInstance()
        minD.set(Calendar.DATE, 1)
        minD.add(Calendar.MONTH, -1)
        val maxD = Calendar.getInstance()
        maxD.add(Calendar.MONTH, -1)
        maxD.set(Calendar.DATE, maxD.getActualMaximum(Calendar.DATE))

        val from = formatter.format(minD.time)
        val to = formatter.format(maxD.time)

        return "$from - $to"
    }

    fun startDateThisMonth(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", currentLocale)

        val minD = Calendar.getInstance()
        minD.set(Calendar.DATE, 1)

        return formatter.format(minD.time)
    }

    fun endDateThisMonth(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", currentLocale)

        val minD = Calendar.getInstance()
        minD.set(Calendar.DATE, minD.getActualMaximum(Calendar.DATE))

        return formatter.format(minD.time)
    }

    fun isToday(value: String?): Boolean {
        if (value.isNullOrEmpty()) return false
        val strDate = convert(value, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd")
        /*val date = toDate(strDate)
        val dateNow = toDate(dateNow())*/

        return strDate == dateNow()
    }

    fun isTimeAfter(value: String?): Boolean {
        if (value.isNullOrEmpty()) return false

        val formatter = SimpleDateFormat("HH:mm:ss", currentLocale)
        val d = formatter.parse(value)
        val n = formatter.parse(timeNow())

        return d.after(n)
    }

    fun toTime(date: String?): Date? {
        if (date.isNullOrEmpty()) return null
        val formatter = SimpleDateFormat("HH:mm:ss", currentLocale)
        return formatter.parse(date)
    }

    fun timeZone(): String {
        val formatter = SimpleDateFormat("Z", Locale.ENGLISH)
        return formatter.format(Date())
    }

    fun timeZoneCode(): String {
        return when {
            timeZone() == "+0700" -> "WIB"
            timeZone() == "+0800" -> "WITA"
            timeZone() == "+0900" -> "WIT"
            else -> "UNKNOWN"
        }
    }

    fun strToLong(dateRaw: String?): Long {
        if (dateRaw == null) return 0L
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", currentLocale)
        var date: Date? = null
        try {
            date = formatter.parse(dateRaw)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return date?.time ?: 0L
    }

    fun strDateToLong(dateRaw: String?): Long {
        if (dateRaw == null) return 0L
        val formatter = SimpleDateFormat("yyyy-MM-dd", currentLocale)
        var date: Date? = null
        try {
            date = formatter.parse(dateRaw)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return date?.time ?: 0L
    }

    fun convert(raw: String?, source: String? = "yyyy-MM-dd HH:mm:ss", dest: String? = "yyyy-MM-dd HH:mm:ss"): String {
        if (raw.isNullOrEmpty()) return ""

        val formatter = SimpleDateFormat(source, currentLocale)

        val date: Date?
        try {
            date = formatter.parse(raw)
        } catch (e: ParseException) {
            e.printStackTrace()
            return ""
        }

        if (date != null) {
            val fShow = SimpleDateFormat(dest, currentLocale)
            return fShow.format(date)
        }

        return ""
    }

    fun convertNow(raw: String?, source: String?): String {
        if (raw.isNullOrEmpty()) return ""

        val strDate = convert(raw, source, "yyyy-MM-dd")
        val strToday = convert(createAt(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd")

        val formatter = SimpleDateFormat(source, currentLocale)

        val date: Date?
        try {
            date = formatter.parse(raw)
        } catch (e: ParseException) {
            e.printStackTrace()
            return ""
        }

        val dest = if (toDate(strDate)?.before(toDate(strToday)) == true) {
            "d MMM yyyy, HH:mm"
        } else {
            "'Hari ini,' HH:mm"
        }

        if (date != null) {
            val fShow = SimpleDateFormat(dest, currentLocale)
            return fShow.format(date)
        }

        return ""
    }

    fun notificationDate(raw: String?) : String {
        val strDate = convert(raw = raw, dest = "yyyy-MM-dd")
        val date = toDate(strDate)
        date?.let {
            return when (dayDifferent(date, Date()).toInt()) {
                0 ->convert(raw = raw, dest = "HH:mm")
                1 -> "Kemarin"
                else -> convert(raw = raw, dest = "d MMMM yyyy")
            }
        }

        return "-"
    }

    fun monthString(): String {
        return convert(fromDate(Date()), "yyyy-MM-dd", "MMMM")
    }

    fun dayInt(): Int {
        return convert(fromDate(Date()), "yyyy-MM-dd", "d").toInt()
    }

    fun monthInt(): Int {
        return convert(fromDate(Date()), "yyyy-MM-dd", "M").toInt()
    }

    fun yearInt(): Int {
        return convert(fromDate(Date()), "yyyy-MM-dd", "yyyy").toInt()
    }

    fun fromDate(date: Date?): String {
        if (date == null) return ""
        val formatter = SimpleDateFormat("yyyy-MM-dd", currentLocale)
        return formatter.format(date)
    }

    fun fromDateYear(date: Date?): String {
        if (date == null) return ""
        val formatter = SimpleDateFormat("yyyy", currentLocale)
        return formatter.format(date)
    }

    fun fromDateMonth(date: Date?): String {
        if (date == null) return ""
        val formatter = SimpleDateFormat("MM", currentLocale)
        return formatter.format(date)
    }

    fun fromDateMonth2(date: Date?): String {
        if (date == null) return ""
        val formatter = SimpleDateFormat("M", currentLocale)
        return formatter.format(date)
    }

    fun toDate(date: String?): Date? {
        if (date.isNullOrEmpty()) return null
        val formatter = SimpleDateFormat("yyyy-MM-dd", currentLocale)
        return formatter.parse(date)
    }

    fun toDateTime(date: String?): Date? {
        if (date.isNullOrEmpty()) return null
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", currentLocale)
        return formatter.parse(date)
    }

    fun toIntMonth(month: String?): Int {
        if (month == null) return 0

        val formatter = SimpleDateFormat("MMMM", currentLocale)

        val date: Date?
        try {
            date = formatter.parse(month)
        } catch (e: ParseException) {
            e.printStackTrace()
            return 0
        }

        if (date != null) {
            val fShow = SimpleDateFormat("M", currentLocale)
            return Integer.parseInt(fShow.format(date)) - 1
        }

        return 0
    }

    fun toPrettyDuration(second: Int?): String {
        if (second == null) return "-"

        val ms = TimeUnit.SECONDS.toMillis(second.toLong())

        val result: String

        when {
            second > 3600 -> result = String.format(
                "%dh%dm%ds",
                TimeUnit.MILLISECONDS.toHours(ms),
                TimeUnit.MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms)),
                TimeUnit.MILLISECONDS.toSeconds(ms) -
                        (
                                TimeUnit.HOURS.toSeconds(TimeUnit.MILLISECONDS.toHours(ms)) +

                                        TimeUnit.MINUTES.toSeconds(
                                            TimeUnit.MILLISECONDS.toMinutes(ms) -
                                                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms))
                                        )


                                )
            )
            second > 60 -> result = String.format(
                "%dm%ds",
                TimeUnit.MILLISECONDS.toMinutes(ms),
                TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms))
            )
            else -> result = String.format("%ds", second)
        }

        return result
    }

    fun prettyRange(start: String?, end: String?): String {

        val dayStart = Calendar.getInstance()
        val dayEnd = Calendar.getInstance()

        toDate(start)?.let { dayStart.time = it }
        toDate(end)?.let { dayEnd.time = it }

        return if (start == end) {
            convert(start, "yyyy-MM-dd", "d MMMM yyyy")
        } else if (dayEnd[Calendar.MONTH] == dayStart[Calendar.MONTH] && dayEnd[Calendar.YEAR] == dayStart[Calendar.YEAR]) {
            "${convert(start, "yyyy-MM-dd", "d")} - ${convert(end, "yyyy-MM-dd", "d MMMM yyyy")}"
        } else if (dayEnd[Calendar.MONTH] != dayStart[Calendar.MONTH] && dayEnd[Calendar.YEAR] == dayStart[Calendar.YEAR]) {
            "${convert(start, "yyyy-MM-dd", "d MMMM")} - ${convert(end, "yyyy-MM-dd", "d MMMM yyyy")}"
        } else {
            "${convert(start, "yyyy-MM-dd", "d MMMM yyyy")} - ${convert(end, "yyyy-MM-dd", "d MMMM yyyy")}"
        }
    }

    fun dayBetween(start: String?, end: String?): Int {

        var dayStart = Date()
        var dayEnd = Date()

        toDate(start)?.let { dayStart = it }
        toDate(end)?.let { dayEnd = it }

        return dayDifferent(dayStart, dayEnd).toInt() + 1
    }

    fun dayBetweenFull(start: String?, end: String?): String {

        var dayStart = Date()
        var dayEnd = Date()

        toDate(start)?.let { dayStart = it }
        toDate(end)?.let { dayEnd = it }

        val day = dayDifferent(dayStart, dayEnd).toInt() + 1
        return if (day > 1) {
            "$day Hari"
        } else {
            "$day Hari"
        }

    }

    fun dayDifferent(start: Date, end: Date): Long {
        return (end.time - start.time) / 86400000
    }

    fun countBeetwenDays(start: Date?, end: Date? = toDate(dateNow())) : Long{
        if (start == null && end == null) return 0L
        val diffInMillies = abs((end?.time?:0L) - (start?.time?:0L))
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS)
    }

    fun validateEmpty(value: String?): String {
        return if (value.isNullOrEmpty()) "-"
        else value
    }

    fun addRp(value: Long?): String {
        val v = value ?: 0L
        return "Rp${String.format(currentLocale, "%,d", v)}"
    }

    fun addNoRp(value: Long?): String {
        val v = value ?: 0L
        return String.format(currentLocale, "%,d", v)
    }
}