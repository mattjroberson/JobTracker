package com.matthew.jobtracker.data

import kotlinx.serialization.Serializable

@Serializable
class Job (
    var name: String = "Default Name",
    var taskList: MutableList<Task> = mutableListOf()
){
    fun getTotalTime() : Long{
        var sum : Long = 0

        taskList.forEach {
            //TODO Handle active task case here
            sum += it.endTime - it.startTime
        }

        return sum
    }
}