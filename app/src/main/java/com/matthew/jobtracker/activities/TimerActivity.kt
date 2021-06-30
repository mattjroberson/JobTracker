package com.matthew.jobtracker.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.matthew.jobtracker.databinding.ActivityTimerBinding

class TimerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityTimerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val args = intent.extras
        binding.textViewJob.text = args?.getString("Job")
        binding.textViewTask.text = args?.getString("Task")
    }
}