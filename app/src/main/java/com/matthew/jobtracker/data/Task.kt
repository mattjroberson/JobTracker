package com.matthew.jobtracker.data

import android.text.format.DateUtils
import kotlinx.serialization.Serializable

@Serializable
data class Task (
    var name: String,
    var parentJob: String,
    var loggedTime: Long,
){
    val prettyTime : String
        get() = DateUtils.formatElapsedTime(loggedTime)
}