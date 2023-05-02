package com.mne4.fromandto.Data.Retrofit2.Models

data class MyOrder(
    val SurnameUser: String?,
    val Image_urlUser: String?,
    val RatingUser: Double?,
    val PhoneUser:String?,
    val SurnameDriver: String?,
    val Image_urlDriver: String?,
    val RatingDriver: Double?,
    val PhoneDriver:String?,
    val Start_Time: String,
    val Price: Float,
    val Descreption: String,
    val Seats_amount:Int,
    val Start_Point: String,
    val End_Point: String?,

)
