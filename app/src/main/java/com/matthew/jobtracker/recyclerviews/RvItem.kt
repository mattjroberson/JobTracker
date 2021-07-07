package com.matthew.jobtracker.recyclerviews

import android.view.View
import com.matthew.jobtracker.databinding.RecyclerViewItemBinding

class RvItem(val title: String, val subText: String, val actionHandler: (type: ActionType) -> Unit) {
    companion object {
        enum class ActionType { EDIT, DELETE }
    }

    fun attach(itemView: View){
        itemView.apply {
            val binding = RecyclerViewItemBinding.bind(this)

            binding.rvText.apply {
                text = title
                setOnClickListener {
                    actionHandler(ActionType.EDIT)
                }
            }

            binding.rvSubtext.text = subText
        }
    }
}