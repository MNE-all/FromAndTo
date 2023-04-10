package com.mne4.fromandto.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoUser {
    @Insert
    fun insertItem(user: User)

    @Query("SELECT * FROM User")
    fun getAllUser(): Flow<List<User>>

}