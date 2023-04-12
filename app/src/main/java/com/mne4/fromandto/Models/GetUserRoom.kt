package com.mne4.fromandto.Models

import androidx.room.ColumnInfo
import java.util.*

data class GetUserRoom (
    var id_user: String,
    var password:String,
    var surname: String,
    var name: String,
    var birthday: String,
    var gender: String,
    var phone: String,
)