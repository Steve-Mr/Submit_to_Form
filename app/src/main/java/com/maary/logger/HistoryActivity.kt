package com.maary.logger

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.maary.logger.databinding.ActivityHistoryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private val transactionViewModel: TransactionViewModel by viewModels {
        TransactionViewModelFactory((application as TransactionApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = TransactionListAdapter()
        binding.historyList.adapter = adapter
        binding.historyList.layoutManager = LinearLayoutManager(this)

        transactionViewModel.recentTransaction.observe(this) { transition ->
            transition?.let { adapter.submitList(it) }
        }

    }
}