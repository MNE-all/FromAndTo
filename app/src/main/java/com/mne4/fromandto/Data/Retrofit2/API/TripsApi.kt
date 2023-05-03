package com.mne4.fromandto.Data.Retrofit2.API

import com.mne4.fromandto.Data.Retrofit2.Models.Trips
import com.mne4.fromandto.Data.Retrofit2.Models.TripsFull
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.*

interface TripsApi {
    @GET("Trips/Read")
    suspend fun getAll(): ArrayList<TripsFull>



    @GET("Trips/Read/{guid}")
    suspend fun getCurrentTrips(@Path("guid") guid:String): TripsFull

    @GET("Trips/ReadByDateStartPointEndPoint/{date}/{start_point}/{end_point}")
    suspend fun getReadByDateStartPointEndPoint(@Path("date") date:String, @Path("start_point") start_point:String,
                         @Path("end_point") end_point: String): ArrayList<Trips>

    @GET("Trips/ReadMyOrdersTripsCurrentUser/{guid}")
    suspend fun getMyOrdersTripsCurrentUser(@Path("guid") guid:String,
                                             @Query("isDriver") isDriver:Boolean): ArrayList<TripsFull>
    @GET("Trips/ReadFromCities")
    suspend fun getCitysFrom( @Query("isDriver") isDriver:Boolean): ArrayList<String>
    @GET("Trips/ReadToCities/{start_point}")
    suspend fun getCitysTo(@Path("start_point") start_point:String,
                            @Query("isDriver") isDriver:Boolean): ArrayList<String>
    @GET("Trips/Read/{date_start}/{date_end}/{start_point}/{end_point}")
    suspend fun getReadDateStartToDateEndToFrom(@Path("date_start") date_start:String,
                                                @Path("date_end") date_end:String,
                                                @Path("start_point") start_point:String,
                                                @Path("end_point") end_point: String): ArrayList<TripsFull>

    @GET("Trips/ReadByDateStartDateEndStartPoint/{date_start}/{date_end}/{start_point}")
    suspend fun getReadByDateStartDateEndFrom(@Path("date_start") date_start:String,
                                          @Path("date_end") date_end:String,
                                          @Path("start_point") start_point:String): ArrayList<TripsFull>
    @GET("Trips/ReadByStart/{start_point}")
    suspend fun getReadByStart(@Path("start_point") start_point:String): ArrayList<TripsFull>
    @GET("Trips/ReadByStartPointEndPoint/{start_point}/{end_point}")
    suspend fun getReadByStartPointEndPoint(@Path("start_point") start_point:String,
                                                @Path("end_point") end_point:String): ArrayList<TripsFull>
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