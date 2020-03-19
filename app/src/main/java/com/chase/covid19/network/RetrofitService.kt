package com.chase.covid19.network

import android.content.Intent
import com.chase.covid19.activities.SignUpActivity
import com.chase.covid19.application.ApplicationLoader
import com.chase.covid19.model.*
import com.chase.covid19.utils.Prefs
import retrofit2.HttpException
import retrofit2.http.*
import java.lang.Exception

interface RetrofitService {
    @GET("register/{mobile}")
    suspend fun register(@Path("mobile") mobile: String)

    @GET("verify/{mobile}/{code}")
    suspend fun verify(@Path("mobile") mobile: String, @Path("code") code: String)

    @POST("submit/{mobile}/{code}/{name}")
    suspend fun submit(@Path("mobile") mobile: String,
                       @Path("code") code: String,
                       @Path("name") name: String,
                       @Query("step") step: String,
                       @Body resultData: ResultData): SubmitResponse

    @GET("names/{mobile}/{code}")
    suspend fun getNames(@Path("mobile") mobile: String,
                         @Path("code") code: String): LinkedHashMap<String?, Long?>?

    @GET("submissions/{mobile}/{code}/{name}")
    suspend fun getTestDetail(@Path("mobile") mobile: String,
                              @Path("code") code: String,
                              @Path("name") name: String): ResultData?

    @GET("config")
    suspend fun config(): ConfigResponse?

    @GET
    suspend fun getNextQuestionPage(@Url url: String): ConfigResponse?

    @GET("questions/{mobile}/{code}/{name}")
    suspend fun getQuestions(@Path("mobile") mobile: String,
                             @Path("code") code: String,
                             @Path("name") name: String,
                             @Query("step") step: String): ConfigResponse?

    @GET("questions/{mobile}/{code}/{name}")
    suspend fun getQuestions1(@Path("mobile") mobile: String,
                              @Path("code") code: String,
                              @Path("name") name: String): ConfigResponse?

    @POST("launch/{mobile}/{code}")
    suspend fun launch(@Path("mobile") mobile: String,
                       @Path("code") code: String,
                       @Body launchModel: LaunchModel)

    @POST("delete/{mobile}/{code}/{name}")
    suspend fun delete(@Path("mobile") mobile: String,
                       @Path("code") code: String,
                       @Path("name") name: String)
}

interface OnApiGeneralEventListener {
    fun onInvalidCode(e: NetworkError)
}

private val apiListeners = HashSet<OnApiGeneralEventListener>()

fun OnApiGeneralEventListener.startListenApi() {
    apiListeners += this
}

fun OnApiGeneralEventListener.stopListenApi() {
    apiListeners -= this
}

suspend fun <T> apiCall(apiFun: suspend RetrofitService.() -> T) = apiCall(apiFun, false)

suspend fun <T> apiCall(apiFun: suspend RetrofitService.() -> T, isVerify: Boolean): Result<T> {
    return try {
        Result.success(apiFun(RetrofitFactory.retrofitService))
    } catch (e: HttpException) {
        val ans = Result.error<T>(null, e)

        val netErr = ans.networkError
        if (!isVerify && apiListeners.isNotEmpty() && netErr != null && netErr.message == "INVALID_VERIFICATION_CODE") {
            Prefs.clear()
            apiListeners.forEach { it.onInvalidCode(netErr) }

            ApplicationLoader.app.run {
                startActivity(Intent(this,
                                     SignUpActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        }
        ans
    } catch (e: Exception) {
        Result.error(null, e)
    }
}