package com.mne4.fromandto.API

import com.mne4.fromandto.Models.Trips
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface TripsApi {
    @GET("Trips/Read")
    suspend fun getAll(): ArrayList<Trips>

    @GET("Trips/Read/{guid}")
    suspend fun getCurrentTrips(@Path("guid") guid:String): Trips

    @GET("Trips/{date}/{start_point}/{end_point}")
    suspend fun getTrips(@Path("date") date:String, @Path("start_point") start_point:String,
                         @Path("end_point") end_point: String): ArrayList<Trips>

    @ExperimentalMultiplatform
        @POST("Trips/CreateTrip")
        fun postCreateTrips(@Query("guid") guid: String, @Body trips: Trips): Call<ResponseBody>

    @ExperimentalMultiplatform
        @POST("Trips/CreateRequest")
        fun postCreateRequest(@Query("guid") guid: String, @Body trips: Trips): Call<ResponseBody>

    @ExperimentalMultiplatform
        @PUT("Trips/Update")
        fun putEditTrips(@Query("guid") guid: String, @Body trips: Trips): Call<ResponseBody>

    @DELETE("Trips/Delete")
    fun deleteTrips(@Query("guid") guid: String): Call<ResponseBody>

}