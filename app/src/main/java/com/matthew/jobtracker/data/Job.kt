package com.matthew.jobtracker.data

import android.text.format.DateUtils
import kotlinx.serialization.Serializable

@Serializable
data class Job (
    var name: String,
    var taskList: MutableList<Task> = mutableListOf()
) {
    val prettyTotalTime : String
        get() = DateUtils.formatElapsedTime(taskList.map{it.loggedTime}.sum())

}