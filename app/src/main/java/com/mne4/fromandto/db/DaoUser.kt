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

//    @Query("UPDATE Users SET password= :password, surname = :surname, name= :name, gender= :gender, birthday= :birthday, phone= :phone WHERE id = :id")
//    fun updateUser(password: String,surname: String, name: String, gender: String, birthday: String, phone:String)

    @Query("SELECT * FROM Users")
    fun getAllUser(): Flow<List<User>>

    @Query("DELETE FROM Users")
    fun deleteAllUser()

}