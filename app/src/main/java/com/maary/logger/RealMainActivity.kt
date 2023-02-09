package com.maary.logger

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFinishOnTouchOutside(false)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.minus.setOnClickListener {
            if (binding.minus.isChecked){
                binding.minus.setIconResource(R.drawable.ic_add)
            }else {
                binding.minus.setIconResource(R.drawable.ic_minus)
            }
        }

        binding.yes.setOnClickListener {

            if (binding.textfieldAmount.editText?.text.toString().trim() != ""){

                var amount = binding.textfieldAmount.editText?.text.toString().trim()
                if (!binding.minus.isChecked) amount = "-$amount"
                val type = binding.menu.editText?.text.toString()

                scope.launch {
                    if (sendPostRequest(amount, type)){
                        finishAndRemoveTask()
                    }
                    else {
                        binding.title.text = resources.getText(R.string.failed)
                    }
                }
            }
        }

        binding.no.setOnClickListener {
            finishAndRemoveTask()
        }

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