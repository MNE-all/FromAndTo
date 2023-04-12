package com.mne4.fromandto.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Users")
data class User (
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo("id_user")
    var id_user: String,
    @ColumnInfo("password")
    var password: String,
    @ColumnInfo("surname")
    var surname: String,
    @ColumnInfo("name")
    var name: String,
    @ColumnInfo("birthday")
    var birthday: String?,
    @ColumnInfo("gender")
    var gender: String?,
    @ColumnInfo("phone")
    var phone: String,
    @ColumnInfo("isInAcc")
    var isInAcc: Boolean = false
)