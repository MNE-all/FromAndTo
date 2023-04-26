package com.mne4.fromandto.Data.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mne4.fromandto.Data.Room.DAO.DaoUser
import com.mne4.fromandto.Data.Room.Entities.User


@Database(
    entities = [
        User::class
    ],
    version = 2
)


abstract class MainDB: RoomDatabase() {
    abstract fun getDao(): DaoUser


    companion object{

        fun getDB(context: Context): MainDB {
            return Room.databaseBuilder(
                context.applicationContext,
                MainDB::class.java,
                "FromAndTo_local.db"
            ).build()
        }
    }

}