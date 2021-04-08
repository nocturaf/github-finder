package com.nocturaf.githubfinder.network.util

/**
 * Wrapper class to determine the network result state
 */
sealed class Result<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T?) : Result<T>(data)
    class Fail<T>(message: String?, data: T? = null) : Result<T>(data, message)
    class Loading<T> : Result<T>()
}