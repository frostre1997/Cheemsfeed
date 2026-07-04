package com.frostre1997.cheemsfeed.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.frostre1997.cheemsfeed.MainActivity
import com.frostre1997.cheemsfeed.R
import com.frostre1997.cheemsfeed.network.RedditApiClient
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private var authManager: RedditAuthManager? = null
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton = findViewById(R.id.btn_login)
        progressBar = findViewById(R.id.progress_bar)

        loginButton.isEnabled = false

        lifecycleScope.launch {
            authManager = RedditAuthManager.create(this@LoginActivity, RedditApiClient.wwwService)
            loginButton.isEnabled = true

            if (authManager?.isLoggedIn() == true) {
                navigateToMain()
                return@launch
            }

            loginButton.setOnClickListener {
                val url = authManager?.getAuthorizationUrl() ?: return@setOnClickListener
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }

            handleAuthCallback(intent)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleAuthCallback(intent)
    }

    private fun handleAuthCallback(intent: Intent?) {
        val uri = intent?.data ?: return
        if (uri.scheme != "cheemsfeed") return

        val code = uri.getQueryParameter("code")
        val state = uri.getQueryParameter("state")
        val error = uri.getQueryParameter("error")

        when {
            error != null -> {
                Toast.makeText(this, "Auth failed: $error", Toast.LENGTH_SHORT).show()
            }
            code != null && state == authManager?.getSavedState() -> {
                authManager?.clearState()
                exchangeCodeForToken(code)
            }
            code != null -> {
                Toast.makeText(this, "State mismatch", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun exchangeCodeForToken(code: String) {
        progressBar.visibility = android.view.View.VISIBLE
        loginButton.isEnabled = false

        lifecycleScope.launch {
            val success = authManager?.exchangeCodeForToken(code) == true
            progressBar.visibility = android.view.View.GONE
            loginButton.isEnabled = true

            if (success) {
                Toast.makeText(this@LoginActivity, "Logged in!", Toast.LENGTH_SHORT).show()
                navigateToMain()
            } else {
                Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
