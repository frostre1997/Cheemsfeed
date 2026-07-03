package com.frostre1997.cheemsfeed

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import android.widget.Spinner
import android.widget.ArrayAdapter
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.button.MaterialButton
import com.google.android.material.appbars.MaterialToolbar

class SettingsActivity : AppCompatActivity() {
    private lateinit var themeSpinner: Spinner
    private lateinit var materialYouToggle: SwitchMaterial
    private lateinit var logoutButton: MaterialButton
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        prefs = getSharedPreferences("settings", MODE_PRIVATE)

        // Setup toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        // Setup theme spinner
        themeSpinner = findViewById(R.id.themeSpinner)
        val themes = arrayOf("Auto (System)", "Light", "Dark")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, themes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        themeSpinner.adapter = adapter

        // Load saved theme
        val savedTheme = prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        val themeIndex = when (savedTheme) {
            AppCompatDelegate.MODE_NIGHT_NO -> 1
            AppCompatDelegate.MODE_NIGHT_YES -> 2
            else -> 0
        }
        themeSpinner.setSelection(themeIndex)

        themeSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                val themeMode = when (position) {
                    1 -> AppCompatDelegate.MODE_NIGHT_NO
                    2 -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
                prefs.edit().putInt("theme_mode", themeMode).apply()
                AppCompatDelegate.setDefaultNightMode(themeMode)
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        // Setup Material You toggle
        materialYouToggle = findViewById(R.id.materialYouToggle)
        materialYouToggle.isChecked = prefs.getBoolean("material_you", android.os.Build.VERSION.SDK_INT >= 31)
        materialYouToggle.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("material_you", isChecked).apply()
            // Restart app to apply Material You colors
            recreate()
        }

        // Setup logout button
        logoutButton = findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {
            prefs.edit().clear().apply()
            finish()
        }
    }
}
