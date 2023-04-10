package com.mne4.fromandto.Models

data class User(
    val surname: String,
    val name: String,
    val gender: String,
    val email: String?,
    val login: String?,
    val password: String,
    val birthday: String,
    val phone: String,
    val isDriver: Boolean,
    val rating: Double?,
    val image_url: String?,
    val passport: String?,
    val licenseval : String?
)