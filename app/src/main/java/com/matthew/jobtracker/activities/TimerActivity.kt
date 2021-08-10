package com.matthew.jobtracker.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.format.DateUtils
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.matthew.jobtracker.helpers.DatabaseHelper
import com.matthew.jobtracker.R
import com.matthew.jobtracker.helpers.SharedPrefsHelper
import com.matthew.jobtracker.data.Job
import com.matthew.jobtracker.data.Task
import com.matthew.jobtracker.data.TimerParams
import com.matthew.jobtracker.databinding.ActivityTimerBinding

class TimerActivity : AppCompatActivity() {

    private var finished = false

    private lateinit var binding : ActivityTimerBinding
    private lateinit var params : TimerParams
    private lateinit var db : DatabaseHelper
    private lateinit var job : Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = intent.extras
        params = args?.getParcelable(ArgConsts.TIMER_PARAMS) ?: return

        db = DatabaseHelper(this)
        job = db.getCurrentJob(params.jobName) ?: Job(params.jobName)

        //If timer was running before startup, get the elapsed time
        if(!params.paused && params.secondsElapsed > 0) calculateTimeElapsed()

        binding = ActivityTimerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.textViewJob.text = params.jobName
        binding.textViewTask.text = params.taskName

        binding.buttonPauseResume.setOnClickListener { onPlayPause() }
        binding.buttonFinish.setOnClickListener { onFinish() }

        onBackPressedDispatcher.addCallback(this) { onBack(this) }

        setPlayPauseButton(params.paused)
        runTimer()
    }

    override fun onStop() {
        super.onStop()
        if(!finished) saveState()
    }

    private fun onPlayPause(){
        params.paused = !params.paused
        setPlayPauseButton(params.paused)
    }

    private fun onFinish(){
        finished = true
        saveTask()
        launchMainActivity()
    }

    private fun onBack(owner : OnBackPressedCallback){
        owner.isEnabled = true
        Toast.makeText(applicationContext, resources.getString(R.string.timer_back_message), Toast.LENGTH_SHORT).show()
    }

    //Save the active state for app reload
    private fun saveState(){
        val sharedPref = getSharedPreferences(SharedPrefsHelper.FILE_NAME, Context.MODE_PRIVATE) ?: return
        SharedPrefsHelper.writeParams(sharedPref, params)
    }

    //Save the completed task on finish to the database
    private fun saveTask(){
        val timeRoundedToMins = roundSecondsToNearestMin(params.secondsElapsed)
        val task = Task(params.taskName, job.name, timeRoundedToMins)

        job.taskList.add(task)
        db.addCurrentJob(job)
    }

    private fun calculateTimeElapsed(){
        val passiveTimeElapsed = System.currentTimeMillis() - params.stoppedTime
        params.secondsElapsed += passiveTimeElapsed / 1000
    }

    private fun roundSecondsToNearestMin(seconds : Long) : Long{
        val trailingSeconds = seconds % 60L
        var minutes = seconds / 60L

        if(trailingSeconds >= 30) minutes++
        return minutes
    }

    private fun setPlayPauseButton(paused : Boolean){
        val textId = if(paused) R.string.button_resume else R.string.button_pause
        binding.buttonPauseResume.text = resources.getString(textId)
    }

    private fun launchMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(ArgConsts.TIMER_FINISHED, true)
        startActivity(intent)
    }

    //Code Design: https://github.com/Aashrut/Android-Stopwatch-App
    private fun runTimer(){
        val handler = Handler()

        handler.post(object : Runnable {
            override fun run() {
                if (!params.paused) params.secondsElapsed+=1

                val formattedTime = DateUtils.formatElapsedTime(params.secondsElapsed)
                binding.textViewTime.text = formattedTime

                handler.postDelayed(this, 1000)
            }
        })
    }
}