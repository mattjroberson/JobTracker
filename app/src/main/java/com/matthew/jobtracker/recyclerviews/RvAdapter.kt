package com.matthew.jobtracker.recyclerviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.matthew.jobtracker.R
import com.matthew.jobtracker.data.rv_items.ItemData

class RvAdapter(private var dataList: MutableList<out ItemData>,
                private var onItemListener: OnItemListener?
) : RecyclerView.Adapter<RvAdapter.RvViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RvViewHolder {
        val layout = R.layout.recycler_view_item
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return RvViewHolder(view, onItemListener)
    }

    override fun onBindViewHolder(holder: RvViewHolder, position: Int) {
        val data = dataList[position]
        holder.title.text = data.text
        holder.info.text = data.subtext
    }

    override fun getItemCount(): Int {
        return dataList.count()
    }

    inner class RvViewHolder(itemView: View, private val onItemListener: OnItemListener?) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener{

        var title : TextView = itemView.findViewById(R.id.rv_text)
        var info : TextView = itemView.findViewById(R.id.rv_subtext)

        init{
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            onItemListener?.onItemClick(adapterPosition)
        }
    }

    interface OnItemListener{
        fun onItemClick(position: Int)
    }
}