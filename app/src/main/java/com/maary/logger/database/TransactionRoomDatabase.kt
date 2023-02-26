package com.maary.logger.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Transaction::class], version = 1, exportSchema = false)
public abstract class TransactionRoomDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: TransactionRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ) : TransactionRoomDatabase{
            val instance = Room.databaseBuilder(
                context.applicationContext,
                TransactionRoomDatabase::class.java,
                "transcation_database"
            ).build()
            INSTANCE = instance
            return instance
        }
    }
//
//    private class TransactionDatabaseCallback(
//        private val scope: CoroutineScope
//    ):RoomDatabase.Callback() {
//
//        override fun onCreate(db: SupportSQLiteDatabase) {
//            super.onCreate(db)
//            INSTANCE?.let {database ->
//                scope.launch {
//
//                }
//            }
//        }
//
//        suspend fun populateDatabase()
//    }
}