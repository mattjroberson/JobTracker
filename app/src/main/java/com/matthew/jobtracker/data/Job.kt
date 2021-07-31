package com.matthew.jobtracker.data

import android.text.format.DateUtils
import kotlinx.serialization.Serializable

@Serializable
data class Job (
    var name: String = "Default Name",
    var taskList: MutableList<Task> = mutableListOf()
) {
    val prettyTotalTime : String
        get() = DateUtils.formatElapsedTime(getTotalTime())

    private fun getTotalTime() : Long{
        var sum : Long = 0
        taskList.forEach { sum += it.loggedTime}
        return sum
    }
}