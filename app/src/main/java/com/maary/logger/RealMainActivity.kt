package com.maary.logger

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.maary.logger.database.Transaction
import com.maary.logger.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class RealMainActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val scope = CoroutineScope(Dispatchers.Main)
    private val transactionViewModel: TransactionViewModel by viewModels {
        TransactionViewModelFactory((application as TransactionApplication).repository)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFinishOnTouchOutside(false)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.title.setOnClickListener{
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.minus.setOnClickListener {
            if (binding.minus.isChecked){
                binding.minus.setIconResource(R.drawable.ic_add)
            }else {
                binding.minus.setIconResource(R.drawable.ic_minus)
            }
        }

        binding.types.setOnClickListener{
            hideKeyboard()
            binding.types.showDropDown()
        }

        val adapter = TransactionListAdapter (
            onItemClick = { handleRecyclerClick() },
            onItemLongClick = { _ -> false})
        binding.lastRecyclerView.adapter = adapter
        binding.lastRecyclerView.layoutManager = LinearLayoutManager(this)

//        binding.lastRecyclerView.setOnTouchListener { view, motionEvent ->
//            if (motionEvent.action == MotionEvent.ACTION_UP)
//                view.performClick()
//            else
//                false
//        }

//        binding.lastRecyclerView.setOnClickListener{
//            scope.launch {
//                for (i in 1..100) {
//                    transactionViewModel.insert(
//                        Transaction(
//                            1919.810,
//                            "test",
//                            1672506000+i*60))
//                }
//            }
//            startActivity(Intent(this, HistoryActivity::class.java))
//        }

        transactionViewModel.lastTransaction.observe(this) { transition ->
            transition?.let { adapter.submitList(it) }
        }

        binding.yes.setOnClickListener {

            if (binding.textfieldAmount.editText?.text.toString().trim() != ""){

                var amount = binding.textfieldAmount.editText?.text.toString().trim()
                if (!binding.minus.isChecked) amount = "-$amount"
                val type = binding.menu.editText?.text.toString()
                binding.yes.isClickable = false

                scope.launch {
                    if (sendPostRequest(amount, type)){
                        transactionViewModel.insert(
                            Transaction(
                                amount.toDouble(),
                                type,
                                (System.currentTimeMillis()/1000).toInt()))
                        finishAndRemoveTask()
                    }
                    else {
                        binding.title.text = resources.getText(R.string.failed)
                        binding.yes.isClickable = true
                    }
                }
            }
        }

        binding.no.setOnClickListener {
            finishAndRemoveTask()
        }

    }

    // 隐藏键盘
    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun handleRecyclerClick(): Boolean {
        scope.launch {
            transactionViewModel.deleteOld()
        }
        startActivity(Intent(this, HistoryActivity::class.java))
        return true
    }

    private suspend fun sendPostRequest(entry487437733: String, entry198297327: String): Boolean = withContext(
        Dispatchers.IO) {
        var success = false
        try {
            val url = URL("https://docs.google.com/forms/d/e/1FAIpQLSeT_mo9REBH_h7gLo03Xb27r_n8Mme49nCzFEmQZhyEnnUWNg/formResponse")
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "POST"
                doOutput = true
                val data = "entry.487437733=${URLEncoder.encode(entry487437733, "UTF-8")}" +
                        "&entry.198297327=${URLEncoder.encode(entry198297327, "UTF-8")}"
                outputStream.write(data.toByteArray())
                outputStream.flush()
                outputStream.close()
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    success = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        success
    }
}