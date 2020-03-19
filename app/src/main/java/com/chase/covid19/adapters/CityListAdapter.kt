package com.chase.covid19.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chase.covid19.model.CityModel

class CityListAdapter : RecyclerView.Adapter<CityListViewHolder>() {
    var onItemClickListener: OnItemClickListener? = null
    private var cities: List<CityModel> = ArrayList()

    fun setData(value: List<CityModel>) {
        cities = value
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return cities.size
    }

    override fun onBindViewHolder(holder: CityListViewHolder, position: Int) {
        holder.bind(cities[position], onItemClickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityListViewHolder {
        return CityListViewHolder.create(parent)
    }

    interface OnItemClickListener {
        fun onItemClick(cityModel: CityModel, position: Int)
    }
}