package com.mne4.fromandto.Models

data class UserFull (
    val id_user: String,
    val surname: String,
    val name: String,
    val gender: String?,
    val birthday: String?,
    val email: String?,
    val password: String,
    val phone: String,
    val isDriver: Boolean = false,
    val raiting: Double?,
    val image_url: String?,
    val passport: String?,
    val license : String?
)

