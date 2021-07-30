package com.matthew.jobtracker.data

import android.text.format.DateUtils
import kotlinx.serialization.Serializable

@Serializable
data class Job (
    var name: String = "Default Name",
    var taskList: MutableMap<String, Task> = mutableMapOf()
) {
    val prettyTotalTime : String
        get() = DateUtils.formatElapsedTime(getTotalTime())

    private fun getTotalTime() : Long{
        var sum : Long = 0
        taskList.values.forEach { sum += it.loggedTime}
        return sum
    }
}