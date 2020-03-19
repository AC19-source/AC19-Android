package com.chase.covid19.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chase.covid19.R
import com.chase.covid19.model.CityModel

class CityListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(cityModel: CityModel?, onItemClickListener: CityListAdapter.OnItemClickListener?) {
        val textViewCityName = itemView.findViewById<TextView>(R.id.textViewCityName)
        textViewCityName.text = cityModel?.City
        itemView.setOnClickListener {
            onItemClickListener?.onItemClick(cityModel!!,adapterPosition)
        }
    }

    companion object {
        fun create(parent: ViewGroup): CityListViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.city_list_item_layout, parent, false)
            return CityListViewHolder(view)
        }
    }
}