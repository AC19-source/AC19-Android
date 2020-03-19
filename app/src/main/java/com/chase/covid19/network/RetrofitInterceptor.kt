package com.chase.covid19.network

import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.HttpException

class RetrofitInterceptor : Interceptor {
    private lateinit var response: Response

    override fun intercept(chain: Interceptor.Chain): Response =
        chain.run {
            var request = chain.request()
            request = request.newBuilder().build()
            try {
                response = chain.proceed(request)

                var tryCount = 0
                while (!response.isSuccessful && tryCount < 2) {
                    tryCount++
                    response = chain.proceed(request)
                }
                return response
            } catch (e: HttpException) {
                throw e
            }
        }
}