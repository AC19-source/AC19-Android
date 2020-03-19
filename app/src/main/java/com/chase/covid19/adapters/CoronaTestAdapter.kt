package com.chase.covid19.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chase.covid19.R
import com.chase.covid19.model.Post
import org.jetbrains.anko.find

interface TestClickCallback {
    fun openTestDetail(item: Post, position: Int)
    fun updateTest(item: Post, position: Int)
    fun removeTest(item: Post, position: Int)
}

class CoronaTestAdapter(
    var list: MutableList<Post>,
    val context: Context,
    val callback: TestClickCallback
) : androidx.recyclerview.widget.RecyclerView.Adapter<CoronaTestAdapter.ItemHolder>() {
    var loadingIndex = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(
            LayoutInflater.from(context).inflate(R.layout.item_test, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun removeItem(pos: Int) {
        this.list.removeAt(pos)
        notifyItemRemoved(pos)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val post = list[position]
        holder.bind(post, position)
    }

    fun updateList(list: MutableList<Post>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun setLoading(index: Int) {
        listOf(loadingIndex, index).filter { 0 <= it && it < list.size }.forEach {
            notifyItemChanged(it)
        }

        loadingIndex = index
    }

    inner class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView = itemView.find<TextView>(R.id.tv_title)
        private val bodyTextView = itemView.find<TextView>(R.id.tv_text)
        private val update = itemView.find<View>(R.id.update)
        private val remove = itemView.find<View>(R.id.iv_close)
        private val loading = itemView.find<View>(R.id.loading)
        private var post: Post? = null
        private var myPos = 0

        init {
            update.setOnClickListener {
                post?.let { callback.updateTest(it, myPos) }
            }

            itemView.setOnClickListener {
                post?.let { callback.openTestDetail(it, myPos) }
            }

            remove.setOnClickListener {
                post?.let { callback.removeTest(it, myPos) }
            }
        }

        fun bind(post: Post, position: Int) {
            this.myPos = position
            this.post = post
            titleTextView.text = post.title
            bodyTextView.text = post.body

            val isLoading = position == loadingIndex
            update.visibility = if (isLoading) View.GONE else View.VISIBLE
            loading.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}