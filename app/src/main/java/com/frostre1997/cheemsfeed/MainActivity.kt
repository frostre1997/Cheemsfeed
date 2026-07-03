package com.frostre1997.cheemsfeed

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchPosts()
    }

    private fun fetchPosts() {
        val call = RetrofitClient.instance.getHotPosts("Doge")

        call.enqueue(object : Callback<RedditResponse> {
            override fun onResponse(call: Call<RedditResponse>, response: Response<RedditResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val posts = body.data.children.map { it.data }
                        recyclerView.adapter = PostAdapter(posts)
                    } else {
                        Toast.makeText(this@MainActivity, "Empty Response", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("CheemsFeed", "API Error: ${response.code()}")
                    Toast.makeText(this@MainActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RedditResponse>, t: Throwable) {
                Log.e("CheemsFeed", "Connection Failed", t)
                Toast.makeText(this@MainActivity, "Errore di rete: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
