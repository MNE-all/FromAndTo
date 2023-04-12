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

    @Query("UPDATE Users SET password= :password, surname = :surname, name= :name, gender= :gender, birthday= :birthday, phone= :phone, isInAcc= :isInAcc WHERE id = :id")
    fun updateUser(id: Int?, password: String,surname: String, name: String, gender: String?, birthday: String?, phone:String, isInAcc: Boolean)

    @Query("SELECT * FROM Users")
    fun getAllUser(): Flow<List<User>>

    @Query("DELETE FROM Users")
    fun deleteAllUser()

}