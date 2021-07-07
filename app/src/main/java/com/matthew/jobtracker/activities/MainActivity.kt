package com.matthew.jobtracker.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.matthew.jobtracker.popups.NewTaskFragment
import com.matthew.jobtracker.R
import com.matthew.jobtracker.databinding.ActivityMainBinding
import com.matthew.jobtracker.popups.NewSettingFragment

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener { createNewTask() }
        navController = findNavController(R.id.nav_host_fragment)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                navController.navigate(R.id.JobSettingsMenuFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun createNewTask() {
        val curFragId = navController.currentDestination?.id

        //TODO: Refactor
        when(curFragId){
            R.id.ActiveJobsFragment -> {
                val newFragment = NewTaskFragment()
                newFragment.show(supportFragmentManager, "new_task")
            }
            R.id.JobSettingsMenuFragment -> {
                val newFragment = NewSettingFragment()
                newFragment.show(supportFragmentManager, "new_job_setting")
            }
            R.id.TaskSettingsMenuFragment -> {
                val newFragment = NewSettingFragment()
                newFragment.show(supportFragmentManager, "new_task_setting")
            }
        }
    }
}