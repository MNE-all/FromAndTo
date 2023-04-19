package com.mne4.fromandto.Models

data class User(
    var surname: String,
    var name: String,
    var gender: String?,
    var birthday: String?,
    var email: String?,
    var password: String,
    var phone: String,
    var isDriver: Boolean = false,
    var raiting: Double?,
    var image_url: String?,
    var passport: String?,
    var license : String?
)
