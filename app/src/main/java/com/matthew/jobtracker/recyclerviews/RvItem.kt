package com.matthew.jobtracker.recyclerviews

import android.view.View

abstract class RvItem(val title: String) {
    abstract fun attach(itemView: View)
}