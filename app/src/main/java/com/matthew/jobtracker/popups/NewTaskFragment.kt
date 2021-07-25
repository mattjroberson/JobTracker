package com.matthew.jobtracker.popups

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.matthew.jobtracker.DatabaseHelper
import com.matthew.jobtracker.R
import com.matthew.jobtracker.activities.TimerActivity
import com.matthew.jobtracker.databinding.DialogNewTaskBinding

//Popup for creating new tasks.
//Call from the fab button.

class NewTaskFragment : DialogFragment(), AdapterView.OnItemSelectedListener {

    companion object{
        const val SELECT_JOB = "Select Job"
        const val SELECT_TASK = "Select Task"
    }

    private var _binding: DialogNewTaskBinding? = null
    private val binding get() = _binding!!

    private lateinit var alertDialog : AlertDialog
    private lateinit var templateMap : MutableMap<String, MutableList<String>>
    private var currentJob = 0
    private var currentTask = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let{
            val builder = AlertDialog.Builder(it)

            val inflater = it.layoutInflater
            _binding = DialogNewTaskBinding.inflate(inflater)

            //Retrieve job templates from database
            val db = DatabaseHelper(requireContext())
            templateMap = db.getTemplates()

            val jobStrings = mutableListOf(SELECT_JOB)
            jobStrings.addAll(templateMap.keys)

            val taskStrings = mutableListOf(SELECT_TASK)

            //Populate the spinners with correct data
            attachSpinner(binding.spinnerDialogJob, it.applicationContext, jobStrings)
            attachSpinner(binding.spinnerDialogTask, it.applicationContext, taskStrings)

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
    private fun attachSpinner(spinner : Spinner, context : Context, items : MutableList<String>){
        ArrayAdapter(
                context,
                android.R.layout.simple_spinner_item,
                items
        ).also { adapter ->
            spinner.adapter = adapter
            spinner.onItemSelectedListener = this
        }
    }

    //Set the correct task list based on the selected job type
    private fun updateTaskSpinner(context : Context, jobString : String){
        val taskStrings = mutableListOf(SELECT_TASK)

        if(jobString != SELECT_JOB) {
            taskStrings.addAll(templateMap[jobString]!!)
        }

        ArrayAdapter(
            context,
            android.R.layout.simple_spinner_item,
            taskStrings
        ).also {
            binding.spinnerDialogTask.adapter = it
            it.notifyDataSetChanged()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, p1: View?, p2: Int, p3: Long) {
        handleValidInput(parent.id, p2)

        //TODO Likely to be a bug here with update orders
        if(parent.id == R.id.spinner_dialog_job){
            Log.i("TEST", parent.getItemAtPosition(p2).toString())
            updateTaskSpinner(requireContext(), parent.getItemAtPosition(p2).toString())
        }
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
