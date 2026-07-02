package com.frostre1997.glacierfeed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// This adapter binds the data to the UI
class PostAdapter(private val postList: List<String>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.textTitle)
        val info: TextView = itemView.findViewById(R.id.textInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        // Here we assign data to the views
        holder.title.text = postList[position]
        holder.info.text = "u/author • r/subreddit"
    }

    override fun getItemCount() = postList.size
}
