package com.mne4.fromandto.db

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class User (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo("id_user")
    var id_user: String,
    @ColumnInfo("surname")
    var surname: String,
    @ColumnInfo("name")
    var name: String,
    @ColumnInfo("birthday")
    var birthday: String,
    @ColumnInfo("gender")
    var gender: String,
    @ColumnInfo("phone")
    var phone: String,
    @ColumnInfo("isInAcc")
    var isInAcc: Boolean
)