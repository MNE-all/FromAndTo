package com.mne4.fromandto.Data.Retrofit2.API

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File

interface ImageAPI {

    @Multipart
    @POST("upload.php")
    fun postImg(@Part file1: MultipartBody.Part): Call<ResponseBody>
}