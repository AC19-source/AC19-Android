package com.chase.covid19.network

import com.chase.covid19.ext.fromJson
import com.chase.covid19.model.NetworkError
import retrofit2.HttpException
import java.lang.Exception

enum class ResultStatus{
    Success,
    Loading,
    Error
}

data class Result<T>(
    var value: T?,
    var error: Any?,
    var status: ResultStatus = ResultStatus.Loading
) {
    var networkError: NetworkError? = null

    companion object {
        fun <T> success(value: T?): Result<T> {
            val ans = Result(value, null, ResultStatus.Success)
            ans.code = 200
            return ans
        }

        fun <T> loading(value: T? = null) = Result(value, null, ResultStatus.Loading)

        fun <T> error(value: T? = null, error: Any?) = Result(value, error, ResultStatus.Error)

        fun <T> error(value: T? = null, error: HttpException): Result<T> {
            val answer = Result(value, error, ResultStatus.Error)
            answer.code = error.code()
            try {
                answer.networkError = error.response()?.errorBody()?.string()?.fromJson()
            }catch (e: Exception){
                // Log exception or remove try/catch
            }

            return answer
        }
    }

    var code : Int = 0
    fun code() = code

    inline val isSuccessful get() = status == ResultStatus.Success
    inline val isLoading get() = status == ResultStatus.Loading
    inline val isError get() = status == ResultStatus.Error

    fun toSuccess(value: T? = null) {
        this.value = value ?: this.value
        this.error = null
        this.status = ResultStatus.Success
    }

    fun toLoading(value: T? = null) {
        this.value = value ?: this.value
        this.status = ResultStatus.Loading
    }

    fun toError(error: Any?, value: T? = null) {
        this.value = value ?: this.value
        this.error = error
        this.status = ResultStatus.Error
    }
}