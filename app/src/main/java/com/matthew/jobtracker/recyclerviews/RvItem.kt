package com.matthew.jobtracker.recyclerviews

import android.view.View
import com.matthew.jobtracker.databinding.RecyclerViewItemBinding

class RvItem(val title: String, val subText: String, val actionHandler: () -> Unit) {

    fun attach(itemView: View){
        itemView.apply {
            val binding = RecyclerViewItemBinding.bind(this)

            binding.rvText.apply {
                text = title
                setOnClickListener {
                    actionHandler()
                }
            }

            binding.rvSubtext.text = subText
        }
    }
}