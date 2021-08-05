package com.crocodic.core.extension

import java.util.*

fun Calendar.between(start: Calendar, end: Calendar): Boolean {
    return if (this == start || this == end) true else this.before(end) && this.after(start)
}