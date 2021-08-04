package com.matthew.jobtracker.popups

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle

import androidx.fragment.app.DialogFragment
import com.matthew.jobtracker.DialogCallback
import com.matthew.jobtracker.R
import com.matthew.jobtracker.databinding.DialogNewSettingBinding

//Popup for creating new tasks.
//Call from the fab button.

class NewSettingFragment() : DialogFragment() {

    private var _binding: DialogNewSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let{
            val builder = AlertDialog.Builder(it)

            val inflater = it.layoutInflater
            _binding = DialogNewSettingBinding.inflate(inflater)

            builder.setView(binding.root)
                .setPositiveButton(R.string.new_setting_save) { _, _ ->
                    val input = binding.editTextSettingName.text.toString()
                    notifyFragment(input)
                }
                .setNegativeButton(R.string.new_setting_discard) { _, _ ->
                    notifyFragment(null)
                }

            // Create the AlertDialog object
            builder.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun notifyFragment(name : String?){
        val callback = targetFragment as? DialogCallback
        callback?.onDialogDismiss(name)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
