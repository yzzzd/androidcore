package com.crocodic.core.extension

import java.util.*

/**
 * Created by @yzzzd on 4/22/18.
 */

fun Calendar.between(start: Calendar, end: Calendar): Boolean {
    return if (this == start || this == end) true else this.before(end) && this.after(start)
}