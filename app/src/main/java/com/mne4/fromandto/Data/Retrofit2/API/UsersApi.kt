package com.mne4.fromandto.Data.Retrofit2.API

import android.util.Pair
import com.mne4.fromandto.Data.Retrofit2.Models.User
import com.mne4.fromandto.Data.Retrofit2.Models.UserFull
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface UsersApi {
    @GET("Users/Read")
    suspend fun getAll(): ArrayList<User>

    @GET("Users/Read/{guid}")
    suspend fun getCurrentUser(@Path("guid") guid:String): User

    @GET("Users/Read/TwoUser")
    suspend fun getTwoUser(@Query("userOne") userOne:String,
                           @Query("userTwo") userTwo:String): ArrayList<UserFull>

    @POST("Users/AuthenticationAuto")
    fun postAuthenticationAuto(@Query("guid") guid:String, @Query("hashPassword") hashPassword:String): Call<Boolean>

    @POST("Users/IsPhoneUnique")
    fun postIsPhoneUnique(@Query("phone") phone:String): Call<Boolean>

    @POST("Users/AuthenticationPhone")
    fun postAuthentication(@Query("phone") phone:String, @Query("password") password:String): Call<ResponseBody>

    @ExperimentalMultiplatform
    @POST("Users/Create")
    fun postNewUser(@Body user: User): Call<ResponseBody>

    @ExperimentalMultiplatform
    @PUT("Users/Update")
    fun putEditUser(@Query("guid") guid: String,  @Query("hashPassword") hashPassword: String,  @Body user: User): Call<ResponseBody>

    @DELETE("Users/Delete")
    fun deleteUser(@Query("guid") guid: String, @Query("password") password: String): Call<Boolean>

    @ExperimentalMultiplatform
    @PUT("Users/UpdateSecure")
    fun putEditUserSecure(@Query("guid") guid: String,  @Query("hashPassword") hashPassword: String,  @Body user: User): Call<ResponseBody>
}