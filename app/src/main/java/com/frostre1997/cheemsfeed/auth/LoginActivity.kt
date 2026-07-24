package com.frostre1997.cheemsfeed.auth

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.frostre1997.cheemsfeed.R
import com.frostre1997.cheemsfeed.network.RedditApiClient

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Since we don't have OAuth credentials yet, show a message
        Toast.makeText(this, "OAuth login is disabled. Using public feed.", Toast.LENGTH_LONG).show()

        // You can keep the login UI but disable the button, or just finish
        // For now, close this activity after showing the message
        findViewById<android.widget.Button>(R.id.login_button)?.setOnClickListener {
            Toast.makeText(this, "Login not available - using public API", Toast.LENGTH_SHORT).show()
            // Optionally finish()
        }
    }
}
