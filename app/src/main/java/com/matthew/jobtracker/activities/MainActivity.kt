package com.matthew.jobtracker.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.matthew.jobtracker.R
import com.matthew.jobtracker.helpers.SharedPrefsHelper
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

        val sharedPref = getSharedPreferences(SharedPrefsHelper.FILE_NAME, Context.MODE_PRIVATE) ?: return
        val isTaskActive = sharedPref.getBoolean(SharedPrefsHelper.TASK_IS_ACTIVE, false)

        if(isTaskActive){
            val intent = Intent(this, TimerActivity::class.java)
            val timerParams = SharedPrefsHelper.readParams(sharedPref)

            intent.putExtra(ArgConsts.TIMER_PARAMS, timerParams)
            startActivity(intent)
        }
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
}