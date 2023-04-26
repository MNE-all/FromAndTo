package com.mne4.fromandto.Data.Retrofit2.API

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query

interface SmsApi {
    @ExperimentalMultiplatform
        @POST("sms/send")
        fun postSMS(@Query("api_id") api_id: String, @Query("to") to: String, @Query("msg")  msg: String, @Query("json") json: Int = 1): Call<ResponseBody>
}