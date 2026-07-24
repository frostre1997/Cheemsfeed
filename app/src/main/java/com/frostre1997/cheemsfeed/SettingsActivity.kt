package com.frostre1997.cheemsfeed

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.frostre1997.cheemsfeed.auth.RedditAuthManager
import com.frostre1997.cheemsfeed.network.RedditApiClient
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        prefs = getSharedPreferences("settings", MODE_PRIVATE)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        val themeSpinner = findViewById<Spinner>(R.id.themeSpinner)
        val themes = arrayOf("Auto (System)", "Light", "Dark")
        themeSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, themes)
            .also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        val savedTheme = prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        themeSpinner.setSelection(
            when (savedTheme) {
                AppCompatDelegate.MODE_NIGHT_NO -> 1
                AppCompatDelegate.MODE_NIGHT_YES -> 2
                else -> 0
            }
        )

        themeSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, pos: Int, id: Long) {
                val mode = when (pos) {
                    1 -> AppCompatDelegate.MODE_NIGHT_NO
                    2 -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
                prefs.edit().putInt("theme_mode", mode).apply()
                AppCompatDelegate.setDefaultNightMode(mode)
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        val materialYouToggle = findViewById<SwitchMaterial>(R.id.materialYouToggle)
        materialYouToggle.isChecked = prefs.getBoolean("material_you", android.os.Build.VERSION.SDK_INT >= 31)
        materialYouToggle.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("material_you", isChecked).apply()
            recreate()
        }

        findViewById<MaterialButton>(R.id.logoutButton).setOnClickListener {
            RedditAuthManager(this, RedditApiClient.publicService).logout()
            finish()
        }
    }
}
