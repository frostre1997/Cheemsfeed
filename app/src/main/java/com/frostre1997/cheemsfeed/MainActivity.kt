package com.frostre1997.cheemsfeed

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingContainer: FrameLayout
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        recyclerView = findViewById(R.id.recyclerView)
        loadingContainer = findViewById(R.id.loadingContainer)
        progressBar = findViewById(R.id.progressBar)

        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchPosts()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun fetchPosts() {
        val call = RetrofitClient.instance.getHotPosts("Doge")

        call.enqueue(object : Callback<RedditResponse> {
            override fun onResponse(
                call: Call<RedditResponse>,
                response: Response<RedditResponse>
            ) {
                loadingContainer.visibility = android.view.View.GONE
                recyclerView.visibility = android.view.View.VISIBLE

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val posts = body.data.children.map { it.data }
                        recyclerView.adapter = PostAdapter(posts)
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Empty Response",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Log.e("CheemsFeed", "API Error: ${response.code()}")
                    Toast.makeText(
                        this@MainActivity,
                        "Error: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<RedditResponse>, t: Throwable) {
                loadingContainer.visibility = android.view.View.GONE
                recyclerView.visibility = android.view.View.VISIBLE

                Log.e("CheemsFeed", "Connection Failed", t)
                Toast.makeText(
                    this@MainActivity,
                    "Network error: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}
