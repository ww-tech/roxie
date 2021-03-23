package com.ww.roxie.coroutines

import kotlinx.coroutines.flow.*

/**
 * roxie
 *
 * @author dragos
 * @since 23.03.2021
 */
inline fun <reified R> Flow<*>.ofType(): Flow<R> {
    return filter {
        it is R
    }.map {
        it as R
    }
}

fun <T> Flow<T>.defaultOnEmpty(default: T): Flow<T> {
    return onEmpty {
        emit(default)
    }
}