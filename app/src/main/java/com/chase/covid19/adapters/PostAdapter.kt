package com.chase.covid19.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chase.covid19.R
import com.chase.covid19.model.Post
import org.jetbrains.anko.find

class PostAdapter(val list: List<Post>, val context: Context) :
    androidx.recyclerview.widget.RecyclerView.Adapter<PostAdapter.ItemHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(LayoutInflater.from(context).inflate(R.layout.item_post, parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val post = list[position]
        holder.bind(post)
    }

    class ItemHolder(itemView: View) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        private val titleTextView = itemView.find<TextView>(R.id.tv_title)
        private val bodyTextView = itemView.find<TextView>(R.id.tv_text)

        fun bind(post: Post) {
            titleTextView.text = post.title
            bodyTextView.text = post.body
        }
    }
}