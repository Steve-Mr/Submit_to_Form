package com.maary.logger

import android.content.DialogInterface
import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.maary.logger.databinding.ActivityHistoryBinding
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private val scope = CoroutineScope(Dispatchers.Main)
    private val transactionViewModel: TransactionViewModel by viewModels {
        TransactionViewModelFactory((application as TransactionApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_history)
        setContentView(binding.root)

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
//                recreate()
                binding.appBarContainer.setBackgroundColor(getColor(R.color.semiTransparent))
                window.decorView.windowInsetsController?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } // Night mode is not active, we're using the light theme
            Configuration.UI_MODE_NIGHT_YES -> {
//                recreate()
                binding.appBarContainer.setBackgroundColor(getColor(R.color.semiBlack))
                window.decorView.windowInsetsController?.setSystemBarsAppearance(
                    0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } // Night mode is active, we're using dark theme
        }

        val typedValue = TypedValue()
        var actionBarHeight = 0
        if(theme.resolveAttribute(com.google.android.material.R.attr.actionBarSize, typedValue, true)){
            actionBarHeight = TypedValue.complexToDimensionPixelOffset(
                typedValue.data, resources.displayMetrics)
        }

        binding.historyList.setOnApplyWindowInsetsListener { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsets.Type.systemBars())
            view.updatePadding(top = insets.top + actionBarHeight, bottom = insets.bottom * 2)
            WindowInsets.CONSUMED
        }

        val divider = MaterialDividerItemDecoration(this@HistoryActivity, LinearLayoutManager.VERTICAL)
        divider.dividerInsetStart = resources.getDimensionPixelSize(R.dimen.divider_padding)
        divider.dividerInsetEnd = resources.getDimensionPixelSize(R.dimen.divider_padding)
        divider.isLastItemDecorated = false
        binding.historyList.addItemDecoration(divider)

        val adapter = TransactionListAdapter(
            onItemClick = { true },
            onItemLongClick = { time -> deleteSelected(time) })
        binding.historyList.adapter = adapter
        binding.historyList.layoutManager = LinearLayoutManager(this)

        transactionViewModel.recentTransaction.observe(this) { transition ->
            transition?.let {
                adapter.submitList(it)
                if (adapter.itemCount == 0) {
                    binding.layoutNoHistory.visibility = View.VISIBLE
                }else {
                    binding.layoutNoHistory.visibility = View.GONE
                }
            }
        }

        binding.appBarContainer.elevation = 8.0f

        binding.closeActivity.setOnClickListener {
            finish()
        }

        binding.buttonClearAll.setOnClickListener {
            deleteAll()
        }

    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun deleteAll() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_dialog_title)
            .setMessage(getString(R.string.delete_all_dialog_message))
            .setPositiveButton(R.string.delete_dialog_positive) { _: DialogInterface, _: Int ->
                GlobalScope.launch {
                    transactionViewModel.deleteAll()
                }
            }
            .setNegativeButton(R.string.delete_dialog_negative) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            .show()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun deleteSelected(time: Int) : Boolean {
        val date = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.getDefault())
            .format(time.toLong()*1000)
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.delete_dialog_title)
            .setMessage(getString(R.string.delete_dialog_message, date))
            .setPositiveButton(R.string.delete_dialog_positive) { _: DialogInterface, _: Int ->
                GlobalScope.launch {
                    transactionViewModel.deleteSelected(time)
                }
          }
            .setNegativeButton(R.string.delete_dialog_negative) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            .show()
        return true
    }
}