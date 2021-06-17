package com.matthew.jobtracker

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.matthew.jobtracker.databinding.DialogNewTaskBinding


//Popup for creating new tasks.
//Call from the fab button.

class NewTaskFragment : DialogFragment(), AdapterView.OnItemSelectedListener {

    private lateinit var alertDialog : AlertDialog
    private var validJob = false
    private var validTask = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let{
            val builder = AlertDialog.Builder(it)

            val inflater = it.layoutInflater
            val binding = DialogNewTaskBinding.inflate(inflater)

            //Populate the spinners with correct data
            attachSpinner(binding.spinnerDialogJob, R.array.jobs_array, it.applicationContext)
            attachSpinner(binding.spinnerDialogTask, R.array.tasks_array, it.applicationContext)

            builder.setView(binding.root)
                .setPositiveButton(R.string.new_task_ok,
                        DialogInterface.OnClickListener { dialog, id ->
                            //Save Task
                        })
                .setNegativeButton(R.string.new_task_cancel,
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
        handleValidInput(parent.id, (p2 != 0))
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    //Check to see that both input fields are valid, update positive button accordingly
    private fun handleValidInput(id : Int, valid : Boolean){
        if(id == R.id.spinner_dialog_job){
            validJob = valid
        }
        else { //id == R.id.spinner_dialog_task
            validTask = valid
        }

        val posBtnEnabled = validJob && validTask

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = posBtnEnabled
    }
}
