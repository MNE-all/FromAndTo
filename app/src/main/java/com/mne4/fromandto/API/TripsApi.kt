package com.mne4.fromandto.API

import com.mne4.fromandto.Models.Trips
import com.mne4.fromandto.Models.User
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface TripsApi {
    @GET("Trips/Read")
    suspend fun GetAll(): ArrayList<Trips>

    @GET("Trips/Read/{guid}")
    suspend fun GetCurrentTrips(@Path("guid") guid:String): Trips

    @ExperimentalMultiplatform
        @POST("Trips/CreateTrip")
        fun PostCreateTrips(@Query("guid") guid: String, @Body trips: Trips): Call<ResponseBody>

    @ExperimentalMultiplatform
        @POST("Trips/CreateRequest")
        fun PostCreateRequest(@Query("guid") guid: String, @Body trips: Trips): Call<ResponseBody>

    @ExperimentalMultiplatform
        @PUT("Trips/Update")
        fun PutEditTrips(@Query("guid") guid: String, @Body user: Trips): Call<ResponseBody>

    @DELETE("Trips/Delete")
    fun DeleteTrips(@Query("guid") guid: String): Call<ResponseBody>

}