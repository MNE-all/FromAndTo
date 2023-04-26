package com.mne4.fromandto.Data.Retrofit2.Models

data class Order(
    val FIO: String,
    val TimeBegin: String,
    val TimeEnd: String,
    val Price: Int,
    val TripBegin: String,
    val TripEnd: String,
    val Mark: Double
)
