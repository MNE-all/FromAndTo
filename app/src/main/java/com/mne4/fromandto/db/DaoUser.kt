package com.mne4.fromandto.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import retrofit2.http.DELETE

@Dao
interface DaoUser {
    @Insert
    fun insertUser(user: User)

    @Query("UPDATE Users SET password= :password, isInAcc= :isInAcc WHERE id_user = :id_user")
    fun updateUser(id_user: String, password: String, isInAcc: Boolean)

    @Query("UPDATE Users SET isInAcc = :isInAcc WHERE id_user = :id_user")
    fun updateUserisAcc(id_user: String, isInAcc: Boolean)

    @Query("SELECT * FROM Users")
    fun getAllUser(): Flow<List<User>>

    @Query("DELETE FROM Users")
    fun deleteAllUser()

}