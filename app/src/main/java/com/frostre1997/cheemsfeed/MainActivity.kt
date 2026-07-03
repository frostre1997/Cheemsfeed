package com.frostre1997.cheemsfeed

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        fetchPosts()
    }
    
    private fun fetchPosts() {
        val call = RetrofitClient.instance.getHotPosts()
        
        // Correzione: deve implementare Callback, non solo Call
        call.enqueue(object : Callback<RedditResponse> {
            override fun onResponse(call: Call<RedditResponse>, response: Response<RedditResponse>) {
                if (response.isSuccessful) {
                    val rawChildren = response.body()?.data?.children ?: emptyList()
                    
                    val posts = rawChildren.map { it.data }
                    
                    val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
                    recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                    recyclerView.adapter = PostAdapter(posts)
                } else {
                    Log.d("CheemsFeed", "Error: ${response.code()}")
                }
            }
     
            override fun onFailure(call: Call<RedditResponse>, t: Throwable) {
                Log.d("CheemsFeed", "Connection Failed: ${t.message}")
            }
        })
    }
}

