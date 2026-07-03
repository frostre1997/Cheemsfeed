package com.frostre1997.cheemsfeed

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
        
        call.enqueue(object : retrofit2.Call<RedditResponse> {
            override fun onResponse(call: retrofit2.Call<RedditResponse>, response: Response<RedditResponse>) {
                
                if (response.isSuccessful) {
                  val posts = response.body()?.data?.children 
                  
                    val recyclerView = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerView)
                recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@MainActivity)
                recyclerView.adapter = PostAdapter(posts)
            } else {
                Log.d("CheemsFeed", "Error: ${response.code()}")
            }

        }
     
        override fun onFailure(call: Call<RedditResponse>, t: Throwable) {
            Log.d("CheemsFeed", "Connection Falied: ${t.message}")
        }
    })
}
