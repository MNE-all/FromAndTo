package com.mne4.fromandto.Data.Retrofit2.Models

data class FindRequest(
    val Id_trip:String,
    val Name: String?,
    val Image_url: String?,
    val Rating: Double?,
    val Phone:String?,
    val Start_Time: String,
    val Price: Float,
    val Descreption: String,
    val Start_Point: String,
    val End_Point: String,
    val Start_Point_Coord: String,
    val End_Point_Coord: String,
    val Seats_amount:Int,
    val Status: Boolean
)
