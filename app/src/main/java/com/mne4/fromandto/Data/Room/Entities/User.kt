package com.mne4.fromandto.Data.Room.Entities

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
    @ColumnInfo("isInAcc")
    var isInAcc: Boolean = false
)