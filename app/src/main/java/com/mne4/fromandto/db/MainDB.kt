package com.mne4.fromandto.db

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


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
                "FromAndTo_local.db"
            ).build()
        }
    }

}