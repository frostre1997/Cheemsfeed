package com.frostre1997.cheemsfeed

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.frostre1997.cheemsfeed.auth.LoginActivity
import com.frostre1997.cheemsfeed.auth.RedditAuthManager
import com.frostre1997.cheemsfeed.network.RedditApiClient
import com.frostre1997.cheemsfeed.viewmodel.FeedViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: FeedViewModel
    private lateinit var adapter: PostAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var loadingView: android.widget.ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(
            this,
            FeedViewModel.Factory(RedditApiClient.oauthService, RedditApiClient.publicService)
        )[FeedViewModel::class.java]

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        loadingView = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = PostAdapter { post ->
            post.url?.let { url ->
                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                startActivity(intent)
            }
        }
        recyclerView.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fab_sort).setOnClickListener {
            showSortDialog()
        }

        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                loadingView.visibility = if (state.isLoading) android.view.View.VISIBLE else android.view.View.GONE
                adapter.submitList(state.posts)
                if (state.error != null) {
                    Toast.makeText(this@MainActivity, state.error, Toast.LENGTH_SHORT).show()
                }
            }
        }

        lifecycleScope.launch {
            val authManager = RedditAuthManager.create(this@MainActivity, RedditApiClient.wwwService)
            viewModel.setAuthManager(authManager)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(android.content.Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.action_login -> {
                startActivity(android.content.Intent(this, LoginActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSortDialog() {
        val options = arrayOf("Hot", "New", "Top")
        val mode = com.frostre1997.cheemsfeed.viewmodel.SortMode.entries
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Sort by")
            .setItems(options) { _, which ->
                viewModel.setSortMode(mode[which])
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.uiState.value.posts.isEmpty()) {
            viewModel.fetchPosts()
        }
    }
}
