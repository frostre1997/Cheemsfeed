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
        
        call.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<RedditResponse>, response: Response<RedditResponse>) {
                if (response.isSuccessful) {
                  val posts = response.body()?.data?.children 
                  posts?.forEach {
                    Log.d("CheemsFeed", "Post Title: ${it.data.title}")
                }
            }

            override fun onFailure(call: Call<RedditResponse>, t: Throwable) {
                Log.d("CheemsFeed", "Connection Falied: ${t.message}")
            }
        })
    }
