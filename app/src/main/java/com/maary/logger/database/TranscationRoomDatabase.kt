package com.maary.logger.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Transaction::class], version = 1, exportSchema = false)
public abstract class TranscationRoomDatabase : RoomDatabase() {

    abstract fun transcationDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: TranscationRoomDatabase? = null

        fun getDatabase(context: Context) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                TranscationRoomDatabase::class.java,
                "transcation_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}