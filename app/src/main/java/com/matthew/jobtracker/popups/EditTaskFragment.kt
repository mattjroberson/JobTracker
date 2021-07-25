package com.matthew.jobtracker.popups

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle

import androidx.fragment.app.DialogFragment
import com.matthew.jobtracker.R
import com.matthew.jobtracker.databinding.DialogEditTaskBinding
import com.matthew.jobtracker.databinding.DialogNewSettingBinding

//Popup for creating new tasks.
//Call from the fab button.

class EditTaskFragment : DialogFragment() {

    private var _binding: DialogEditTaskBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let{
            val builder = AlertDialog.Builder(it)

            val inflater = it.layoutInflater
            _binding = DialogEditTaskBinding.inflate(inflater)

            builder.setView(binding.root)
                .setPositiveButton(R.string.new_setting_save) { _, _ ->
                    //Save Task
                }
                .setNegativeButton(
                    R.string.new_setting_discard,
                    DialogInterface.OnClickListener { dialog, id ->
                            //Delete Task
                        })

            // Create the AlertDialog object
            builder.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}