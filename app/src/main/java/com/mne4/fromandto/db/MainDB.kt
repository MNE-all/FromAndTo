package com.mne4.fromandto.db

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        User::class
    ],
    version = 2
)
abstract class MainDB: RoomDatabase() {
    abstract fun getDao():DaoUser
    companion object{
        fun getDB(context: Context):MainDB{
            return Room.databaseBuilder(
                context.applicationContext,
                MainDB::class.java,
                "Users.db"
            ).build()
        }
    }

}