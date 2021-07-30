package com.matthew.jobtracker.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TimerParams (
    var taskName : String = "Task",
    var jobName : String = "Job",
    var paused : Boolean = false,
    var secondsElapsed : Long = 0,
    val stoppedTime : Long = 0
    ) : Parcelable