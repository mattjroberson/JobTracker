package com.matthew.jobtracker.recyclerviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.matthew.jobtracker.R

class RvAdapter(var rvItems: List<RvItem>, private var view: View) : RecyclerView.Adapter<RvAdapter.RvViewHolder>(){
    inner class RvViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvViewHolder {
        val layout = R.layout.recycler_view_item
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return RvViewHolder(view)
    }

    override fun onBindViewHolder(holder: RvViewHolder, position: Int) {
        rvItems[position].attach(holder.itemView)
    }

    override fun getItemCount(): Int {
        return rvItems.count()
    }
}