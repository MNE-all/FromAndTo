package com.mne4.fromandto.Data.Retrofit2.Models

data class TripsFull (
    val id_trip:String,
    val driver_id:String?,
    val client_id:String?,
    val price: Float,
    val description:String,
    val start_time:String,
    val seats_amount: Int,
    val start_point: String,
    val end_point: String,
    val status: String
)