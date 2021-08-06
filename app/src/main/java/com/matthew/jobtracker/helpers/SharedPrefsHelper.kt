package com.matthew.jobtracker.helpers

import android.content.SharedPreferences
import com.matthew.jobtracker.data.TimerParams

class SharedPrefsHelper {
    companion object{
        const val FILE_NAME = "com.matthew.jobtracker.prefs"
        const val TASK_IS_ACTIVE = "is_active"

        private const val TASK_NAME = "pref_task"
        private const val JOB_NAME = "pref_job"
        private const val ELAPSED_TIME = "pref_elapsed_time"
        private const val IS_PAUSED = "pref_paused"
        private const val STOPPED_TIME = "pref_stopped_time"

        fun readParams(sharedPref : SharedPreferences) : TimerParams {
            val activeJobName = sharedPref.getString(JOB_NAME, "Unknown")!!
            val activeTaskName = sharedPref.getString(TASK_NAME, "Unknown")!!
            val elapsedTime = sharedPref.getLong(ELAPSED_TIME, 0)
            val isPaused = sharedPref.getBoolean(IS_PAUSED, false)
            val stoppedTime = sharedPref.getLong(STOPPED_TIME, 0)

            return TimerParams(activeTaskName, activeJobName, isPaused, elapsedTime, stoppedTime)
        }

        fun writeParams(sharedPrefs : SharedPreferences, params : TimerParams){
            with (sharedPrefs.edit()) {
                putBoolean(TASK_IS_ACTIVE, true)

                putString(JOB_NAME, params.jobName)
                putString(TASK_NAME, params.taskName)
                putBoolean(IS_PAUSED, params.paused)
                putLong(ELAPSED_TIME, params.secondsElapsed)
                putLong(STOPPED_TIME, System.currentTimeMillis())

                apply()
            }
        }
    }
}