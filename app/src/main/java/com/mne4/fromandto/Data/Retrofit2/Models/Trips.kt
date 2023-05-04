package com.mne4.fromandto.Data.Retrofit2.Models

data class Trips (
    val price: Float,
    val description:String,
    val start_time:String,
    val seats_amount: Int,
    val start_point: String,
    val start_point_coord: String,
    val end_point: String,
    val end_point_coord: String
)