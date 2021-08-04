package com.matthew.jobtracker.popups

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.matthew.jobtracker.DialogCallback
import com.matthew.jobtracker.R
import com.matthew.jobtracker.databinding.DialogEditTaskBinding


//Popup for creating new tasks.
//Call from the fab button.

class EditTaskFragment : DialogFragment() {

    private lateinit var taskName : String
    private lateinit var prettyTime : String

    private var _binding: DialogEditTaskBinding? = null
    private val binding get() = _binding!!

    companion object{
        const val TASK_NAME = "task_name"
        const val PRETTY_TIME = "pretty_time"
        const val MAX_HOURS = 10

        fun newInstance(taskName: String, prettyTime: String) : EditTaskFragment{
            val frag = EditTaskFragment()
            val args = Bundle()
            args.putString(TASK_NAME, taskName)
            args.putString(PRETTY_TIME, prettyTime)
            frag.arguments = args
            return frag
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        readBundle(arguments)

        return activity?.let{
            val builder = AlertDialog.Builder(it)
            val inflater = it.layoutInflater

            _binding = DialogEditTaskBinding.inflate(inflater)
            binding.textViewTaskName.text = taskName
            binding.editTextNewTime.setText(prettyTime)

            builder.setView(binding.root)
                .setPositiveButton(R.string.new_setting_save) { _, _ ->
                    val input = binding.editTextNewTime.text.toString()
                    saveData(input)
                }
                .setNegativeButton(R.string.new_setting_discard) { _, _ -> }

            // Create the AlertDialog object
            builder.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun readBundle(bundle: Bundle?) {
        taskName = bundle?.getString(TASK_NAME) ?: "NOT FOUND"
        prettyTime = bundle?.getString(PRETTY_TIME) ?: "00:00"
    }

    private fun isInputValid(input: String) : Boolean{
        var valid = true
        val inputParts = input.split(":")

        if(inputParts.size != 2) {
            valid = false
        }
        else {
            val hours = inputParts[0].toIntOrNull()
            val minutes = inputParts[1].toIntOrNull()

            if(hours == null || hours < 0 || hours > MAX_HOURS) valid = false
            else if(minutes == null || minutes < 0 || minutes >= 60) valid = false
        }

        if(!valid){
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.invalid_time_format),
                Toast.LENGTH_SHORT
            ).show()
        }
        return valid
    }

    private fun convertPrettyTimeToMinutes(input: String) : Long?{
        if(!isInputValid(input)) return null

        val inputParts = input.split(":")
        val hours = inputParts[0].toLong()
        val minutes = inputParts[1].toLong()

        return minutes + hours * 60L
    }

    private fun saveData(prettyTime: String){
        val newMinutes = convertPrettyTimeToMinutes(prettyTime) ?: return
        notifyFragment(newMinutes.toString())
    }

    private fun notifyFragment(prettyTime: String){
        val callback = targetFragment as? DialogCallback
        callback?.onDialogDismiss(prettyTime)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
