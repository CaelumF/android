package com.toggl.common

sealed class Either<out L, out R> {
    data class Left<out T>(val value: T) : Either<T, Nothing>()
    data class Right<out T>(val value: T) : Either<Nothing, T>()
}

fun <L, R> List<Either<L, R>>.mapLeft(): List<L> =
    filterIsInstance<Either.Left<L>>().map { it.value }

fun <L, R> List<Either<L, R>>.mapRight(): List<R> =
    filterIsInstance<Either.Right<R>>().map { it.value }
