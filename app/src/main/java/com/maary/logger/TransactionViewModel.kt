package com.maary.logger

import androidx.lifecycle.*
import com.maary.logger.database.Transaction
import com.maary.logger.database.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TransactionViewModel(private val transactionRepository: TransactionRepository) : ViewModel() {
    val lastTransaction: LiveData<List<Transaction>> = transactionRepository.lastTransaction.asLiveData()
    val recentTransaction: LiveData<List<Transaction>> = transactionRepository.recentTransactions.asLiveData()

    fun insert(transaction: Transaction) = viewModelScope.launch(Dispatchers.IO) {
        transactionRepository.insert(transaction)
    }

    fun deleteOld() = viewModelScope.launch(Dispatchers.IO) {
        transactionRepository.deleteOldSubmit()
    }

    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        transactionRepository.deleteAll()
    }

    fun deleteSelected(selected: Int) = viewModelScope.launch(Dispatchers.IO) {
        transactionRepository.deleteSelected(selected)
    }
}

class TransactionViewModelFactory(private val transactionRepository: TransactionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(transactionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}