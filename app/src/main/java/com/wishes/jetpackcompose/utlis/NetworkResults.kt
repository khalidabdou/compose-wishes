package com.wishes.jetpackcompose.utlis
import java.io.Serializable

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
    val errorBody: ErrorHandler? = null
) {
    class Idle<T> : Resource<T>()
    class Loading<T>() : Resource<T>()
    class Success<T>(data: T?) : Resource<T>(data)
    class Error<T>(message: String?, errorBody: ErrorHandler?) :
        Resource<T>(message = message, errorBody = errorBody)
}

data class ErrorHandler(
    var statusCode: Int = 0,
    var message: String = "",
    var errors: List<ErrorFormHandler>? = null
) : Serializable

@kotlinx.serialization.Serializable
data class ErrorFormHandler(
//    var code:Int = 0,
    var field: String? = null,
    var message: String? = null,
) : Serializable


