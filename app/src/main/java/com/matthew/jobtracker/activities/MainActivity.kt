package com.matthew.jobtracker.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.matthew.jobtracker.R
import com.matthew.jobtracker.data.TimerParams
import com.matthew.jobtracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private var args : Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        args = intent.extras

        val binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        navController = findNavController(R.id.nav_host_fragment)
    }

    override fun onStart() {
        super.onStart()
        checkForActiveTimer()
    }

    private fun checkForActiveTimer(){
        //Don't check if coming back from finished TimerActivity
        val isTaskFinished = args?.getBoolean(ArgConsts.TIMER_FINISHED) ?: false
        if(isTaskFinished) return

        val sharedPref = getSharedPreferences(ArgConsts.PREF_FILE_NAME, Context.MODE_PRIVATE) ?: return
        val isTaskActive = sharedPref.getBoolean(ArgConsts.PREF_TASK_IS_ACTIVE, false)

        if(isTaskActive){
            val intent = Intent(this, TimerActivity::class.java)
            val timerParams = loadTimerParams(sharedPref)

            intent.putExtra(ArgConsts.TIMER_PARAMS, timerParams)
            startActivity(intent)
        }
    }

    private fun loadTimerParams(sharedPref : SharedPreferences) : TimerParams {
        val activeJobName = sharedPref.getString(ArgConsts.PREF_JOB_NAME, "Unknown")!!
        val activeTaskName = sharedPref.getString(ArgConsts.PREF_TASK_NAME, "Unknown")!!
        val elapsedTime = sharedPref.getLong(ArgConsts.PREF_ELAPSED_TIME, 0)
        val isPaused = sharedPref.getBoolean(ArgConsts.PREF_IS_PAUSED, false)
        val stoppedTime = sharedPref.getLong(ArgConsts.PREF_STOPPED_TIME, 0)

        return TimerParams(activeTaskName, activeJobName, isPaused, elapsedTime, stoppedTime)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                navController.navigate(R.id.JobSettingsMenuFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

object ArgConsts{
    const val TIMER_FINISHED = "timer_finished"
    const val TIMER_PARAMS = "timer_params"

    const val PREF_FILE_NAME = "com.matthew.jobtracker.prefs"

    const val PREF_TASK_IS_ACTIVE = "is_active"
    const val PREF_TASK_NAME = "pref_task"
    const val PREF_JOB_NAME = "pref_job"
    const val PREF_ELAPSED_TIME = "pref_elapsed_time"
    const val PREF_IS_PAUSED = "pref_paused"
    const val PREF_STOPPED_TIME = "pref_stopped_time"
}