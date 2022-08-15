package com.crocodic.core.extension

class Ternary<T>(private val expression: Boolean, private val then: T) {
    operator fun div(`else`: T): T = if (expression) then else `else`
}

operator fun <T> Boolean.rem(a: T): Ternary<T> = Ternary(this, a)