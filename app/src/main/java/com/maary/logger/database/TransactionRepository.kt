package com.maary.logger.database

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {

    val recentTransactions: Flow<List<Transaction>> = transactionDao.getRecentSubmit()

    val lastTransaction: Flow<List<Transaction>> = transactionDao.getLastSubmit()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(transaction: Transaction) {
        transactionDao.insert(transaction)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteOldSubmit() {
        transactionDao.deleteOldSubmit()
    }
}