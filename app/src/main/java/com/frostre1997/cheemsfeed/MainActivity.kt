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
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if (response.isSuccessful) {
                    Log.d("CheemsFeed", "Dati ricevuti con successo!")
                } else {
                    Log.e("CheemsFeed", "Errore: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Log.e("CheemsFeed", "Errore di connessione: ${t.message}")
            }
        })
    }
}
