package com.maary.logger

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*

class SettingsActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content, SettingsFragment())
            .commit()
    }

    class SettingsFragment : PreferenceFragmentCompat(),
            SharedPreferences.OnSharedPreferenceChangeListener {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            addPreferencesFromResource(R.xml.prefs)
            val sharedPreferences = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }
            sharedPreferences?.registerOnSharedPreferenceChangeListener(this)

            updateSummary()
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
            updateSummary()
        }

        private fun updateSummary() {
            val sharedPreferences = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }

            val entries = sharedPreferences?.getStringSet("entries", setOf<String>())?.toMutableSet()
            if (entries?.size != 0){
                findPreference<ListPreference>("entry_list")?.entries = entries?.toTypedArray()
                findPreference<ListPreference>("entry_list")?.entryValues = entries?.toTypedArray()
                findPreference<ListPreference>("entry_list")?.isEnabled = true
            }

            val newEntry = sharedPreferences?.getString("new_entry", "")
            if (newEntry != ""){
                entries?.add(newEntry)
                sharedPreferences?.edit()?.putStringSet("entries", entries)?.apply()
                sharedPreferences?.edit()?.putString("new_entry", "")?.apply()
            }

        }

    }
}