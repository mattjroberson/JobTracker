package com.matthew.jobtracker.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.format.DateUtils
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.matthew.jobtracker.DatabaseHelper
import com.matthew.jobtracker.R
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

        if(!params.paused && params.secondsElapsed > 0) calculateTimeElapsed()

        binding = ActivityTimerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.textViewJob.text = params.jobName
        binding.textViewTask.text = params.taskName

        db = DatabaseHelper(this)
        job = db.getCurrentJob(params.jobName) ?: Job(params.jobName)

        updatePlayPauseButton()

        binding.buttonPauseResume.setOnClickListener {
            params.paused = !params.paused
            updatePlayPauseButton()
        }

        binding.buttonFinish.setOnClickListener {
            finished = true
            saveTask()

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(ArgConsts.TIMER_FINISHED, true)
            startActivity(intent)
        }

        onBackPressedDispatcher.addCallback(this) {
            this.isEnabled = true
            Toast.makeText(applicationContext, resources.getString(R.string.timer_back_message), Toast.LENGTH_SHORT).show()
        }

        runTimer()
    }

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

    private fun updatePlayPauseButton(){
        val textId = if(params.paused) R.string.button_resume else R.string.button_pause
        binding.buttonPauseResume.text = resources.getString(textId)
    }

    override fun onStop() {
        super.onStop()
        if(!finished) saveState()
    }

    //https://github.com/Aashrut/Android-Stopwatch-App
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

    private fun saveState(){
        val sharedPref = getSharedPreferences(ArgConsts.PREF_FILE_NAME, Context.MODE_PRIVATE) ?: return

        with (sharedPref.edit()) {
            putBoolean(ArgConsts.PREF_TASK_IS_ACTIVE, true)

            putString(ArgConsts.PREF_JOB_NAME, params.jobName)
            putString(ArgConsts.PREF_TASK_NAME, params.taskName)
            putBoolean(ArgConsts.PREF_IS_PAUSED, params.paused)
            putLong(ArgConsts.PREF_ELAPSED_TIME, params.secondsElapsed)
            putLong(ArgConsts.PREF_STOPPED_TIME, System.currentTimeMillis())

            apply()
        }
    }
}