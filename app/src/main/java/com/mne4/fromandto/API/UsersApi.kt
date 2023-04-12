package com.mne4.fromandto.API

import com.mne4.fromandto.Models.User
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface UsersApi {
    @GET("Users/Read")
    suspend fun getAll(): ArrayList<User>

    @GET("Users/Read/{guid}")
    suspend fun getCurrentUser(@Path("guid") guid:String): User

    @GET("Users/Authentication/{guid}/{hashPassword}")
    suspend fun getAuthentication(@Path("guid") guid:String, @Path("hashPassword") hashPassword:String): Boolean

    @ExperimentalMultiplatform
    @POST("Users/Create")
    fun postNewUser(@Body user: User): Call<ResponseBody>

    @ExperimentalMultiplatform
    @PUT("Users/Update")
    fun putEditUser(@Query("guid") guid: String,  @Query("hashPassword") hashPassword: String,  @Body user: User): Call<ResponseBody>

    @DELETE("Users/Delete")
    fun deleteUser(@Query("guid") guid: String, @Query("password") password: String): Call<ResponseBody>
}