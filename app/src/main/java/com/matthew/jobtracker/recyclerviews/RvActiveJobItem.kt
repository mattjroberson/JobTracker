package com.matthew.jobtracker.recyclerviews

import android.view.View
import com.matthew.jobtracker.databinding.ItemActiveJobBinding

class RvActiveJobItem(
        trackName: String,
        val actionHandler: (item: RvActiveJobItem, type: ActionType) -> Unit) : RvItem(trackName) {

    companion object {
        enum class ActionType { EDIT, DELETE }
    }

    override fun attach(itemView: View) {
        itemView.apply {
            val binding = ItemActiveJobBinding.bind(this)

            binding.textViewJob.apply {
                text = title
                setOnClickListener {
                    actionHandler(this@RvActiveJobItem, ActionType.EDIT)
                }
            }
        }
    }
}