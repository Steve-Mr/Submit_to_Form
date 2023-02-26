package com.maary.logger

import android.app.Application
import com.maary.logger.database.TransactionRepository
import com.maary.logger.database.TransactionRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class TransactionApplication: Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { TransactionRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { TransactionRepository(database.transactionDao()) }
}