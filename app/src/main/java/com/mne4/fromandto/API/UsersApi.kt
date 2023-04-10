package com.mne4.fromandto.API

import com.mne4.fromandto.Models.User
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface UsersApi {
    @GET("Users/Read")
    suspend fun GetAll(): ArrayList<User>

    @GET("Users/Read/{guid}")
    suspend fun GetCurrentUser(@Path("guid") guid:String): User

    @ExperimentalMultiplatform
    @POST("Users/Create")
    fun PostNewUser(@Body user: User): Call<ResponseBody>

    @ExperimentalMultiplatform
    @PUT("Users/Update")
    fun PutEditUser(@Query("guid") guid: String, @Body user: User): Call<ResponseBody>

    @DELETE("Users/Delete")
    fun DeleteUser(@Query("guid") guid: String): Call<ResponseBody>
}