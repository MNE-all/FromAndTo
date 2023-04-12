package com.mne4.fromandto.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import retrofit2.http.DELETE

@Dao
interface DaoUser {
    @Insert
    fun insertUser(user: User)

    @Query("SELECT * FROM Users")
    fun getAllUser(): Flow<List<User>>

    @Query("DELETE FROM Users")
    fun deleteAllUser()

}