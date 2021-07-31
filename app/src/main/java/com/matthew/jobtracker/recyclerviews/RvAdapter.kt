package com.matthew.jobtracker.recyclerviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.matthew.jobtracker.R

class RvAdapter(private var titleList: List<String>,
                private var infoList: List<String>
) : RecyclerView.Adapter<RvAdapter.RvViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvViewHolder {
        val layout = R.layout.recycler_view_item
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return RvViewHolder(view)
    }

    override fun onBindViewHolder(holder: RvViewHolder, position: Int) {
        holder.title.text = titleList[position]
        holder.info.text = infoList[position]
    }

    override fun getItemCount(): Int {
        return titleList.count()
    }

    inner class RvViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var title : TextView = itemView.findViewById(R.id.rv_text)
        var info : TextView = itemView.findViewById(R.id.rv_subtext)
        var parentLayout : ConstraintLayout = itemView.findViewById(R.id.rv_parent_layout)
    }
}