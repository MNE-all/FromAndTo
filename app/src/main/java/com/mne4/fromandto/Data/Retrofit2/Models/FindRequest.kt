package com.mne4.fromandto.Data.Retrofit2.Models

data class FindRequest(
    val Surname: String,
    val Image_url: String,
    val Rating: Double,
    val Phone:String,

    val TimeBegin: String,
    val TimeEnd: String,
    val Price: Int,
    val TripBegin: String,
    val TripEnd: String,
    val Mark: Double
)
