package com.frostre1997.cheemsfeed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import android.widget.TextView
import com.frostre1997.cheemsfeed.databinding.ItemPostBinding
import java.text.SimpleDateFormat
import java.util.*

class PostAdapter(private val posts: List<PostData>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount() = posts.size

    class PostViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: PostData) {
            binding.postTitle.text = post.title
            binding.postAuthor.text = "u/${post.author ?: "[deleted]"}"
            binding.postDate.text = formatTime(post.created_utc ?: 0L)
            
            binding.scoreButton.text = "👍 ${post.score ?: 0}"
            binding.commentsButton.text = "💬 ${post.num_comments ?: 0}"
        }

        private fun formatTime(timestamp: Long): String {
            if (timestamp == 0L) return "Unknown"
            val date = Date(timestamp * 1000)
            val now = Date()
            val diff = (now.time - date.time) / 1000

            return when {
                diff < 60 -> "now"
                diff < 3600 -> "${diff / 60}m ago"
                diff < 86400 -> "${diff / 3600}h ago"
                diff < 604800 -> "${diff / 86400}d ago"
                else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(date)
            }
        }
    }
}
