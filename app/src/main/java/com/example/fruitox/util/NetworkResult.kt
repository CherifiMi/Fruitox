package com.example.fruitox.util

sealed class NetworkResult<T>(
    val data: T? = null,
    val message : String? = null
){

    // ? the responses from the api

    class Success<T>(data: T): NetworkResult<T>(data)
    class Error<T>(message: String?, data: T?=null): NetworkResult<T>(data,message)
    class Loading<T>: NetworkResult<T>()
}