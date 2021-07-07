package com.matthew.jobtracker.popups

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.matthew.jobtracker.R
import com.matthew.jobtracker.activities.TimerActivity
import com.matthew.jobtracker.databinding.DialogNewTaskBinding

//Popup for creating new tasks.
//Call from the fab button.

class NewTaskFragment : DialogFragment(), AdapterView.OnItemSelectedListener {

    private var _binding: DialogNewTaskBinding? = null
    private val binding get() = _binding!!

    private lateinit var alertDialog : AlertDialog
    private var currentJob = 0
    private var currentTask = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let{
            val builder = AlertDialog.Builder(it)

            val inflater = it.layoutInflater
            _binding = DialogNewTaskBinding.inflate(inflater)

            //Populate the spinners with correct data
            attachSpinner(binding.spinnerDialogJob, R.array.jobs_array, it.applicationContext)
            attachSpinner(binding.spinnerDialogTask, R.array.tasks_array, it.applicationContext)

            builder.setView(binding.root)
                .setPositiveButton(R.string.new_task_ok) { _, _ ->
                    launchTimerActivity(it)
                }
                .setNegativeButton(
                    R.string.new_task_cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                            //Delete Task
                        })

            // Create the AlertDialog object
            alertDialog = builder.create()

            //Disable "OK" initially
            alertDialog.setOnShowListener{
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
            }
            return alertDialog

        } ?: throw IllegalStateException("Activity cannot be null")
    }

    //Create ArrayAdapter and attach to a Spinner
    private fun attachSpinner(spinner : Spinner, content_id : Int, context : Context){
        ArrayAdapter.createFromResource(
                context,
                content_id,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            spinner.onItemSelectedListener = this
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, p1: View?, p2: Int, p3: Long) {
        handleValidInput(parent.id, p2)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    //Check to see that both input fields are valid, update positive button accordingly
    private fun handleValidInput(id : Int, index : Int){
        if(id == R.id.spinner_dialog_job){
            currentJob = index
        }
        else { //id == R.id.spinner_dialog_task
            currentTask = index
        }

        val posBtnEnabled = currentJob != 0 && currentTask != 0
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = posBtnEnabled
    }

    //Starts the timer activity with the selected inputs
    private fun launchTimerActivity(activity : FragmentActivity){
        val intent = Intent(activity, TimerActivity::class.java)

        val jobString = binding.spinnerDialogJob.getItemAtPosition(currentJob).toString()
        intent.putExtra("Job", jobString)

        val taskString = binding.spinnerDialogTask.getItemAtPosition(currentTask).toString()
        intent.putExtra("Task", taskString)

        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
