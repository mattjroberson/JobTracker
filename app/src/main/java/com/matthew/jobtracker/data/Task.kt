package com.matthew.jobtracker.data

import kotlinx.serialization.Serializable

@Serializable
data class Task (
    var taskName: String = "Default Name",
    var startTime: Long,
    var endTime: Long
)