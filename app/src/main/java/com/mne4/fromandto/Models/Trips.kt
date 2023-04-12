package com.mne4.fromandto.Models

data class Trips (
    val price: Float,
    val description:String,
    val start_time:String,
    val seats_amount: Int,
    val start_point: String,
    val end_point: String,
    val status: String
)