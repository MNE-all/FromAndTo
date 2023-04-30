package com.mne4.fromandto.Data.Retrofit2.Models

data class FindRequest(
    val Surname: String,
    val Image_url: String,
    val Rating: Double,
    val Phone:String,
    val Mark: Double,
    val TimeBegin: String,
    val Price: Int,
    val Descreption: String,
    val TripBegin: String,
    val TripEnd: String,
    val Seats_amount:Int,
    val Status: Boolean
)