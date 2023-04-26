package com.mne4.fromandto.Data.Room.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.mne4.fromandto.Data.Room.Entities.User
import kotlinx.coroutines.flow.Flow

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

    @Delete
    fun deleteAllUser(user: User)

}