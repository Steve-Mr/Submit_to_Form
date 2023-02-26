package com.maary.logger.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transaction_table ORDER BY time")
    fun getRecentSubmit(): Flow<List<Transaction>>

    @Query("SELECT * FROM transaction_table ORDER BY time DESC LIMIT 1")
    fun getLastSubmit(): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(transaction: Transaction)

    @Query("DELETE FROM transaction_table WHERE strftime('%s', 'now') - time > 30*24*60*60")
    suspend fun deleteOldSubmit()
}