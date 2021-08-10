package com.matthew.jobtracker.navdestinations

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.matthew.jobtracker.helpers.DialogCallback
import com.matthew.jobtracker.data.JobTemplate
import com.matthew.jobtracker.data.rv_items.TaskSettingItemData
import com.matthew.jobtracker.databinding.FragmentTaskSettingsBinding
import com.matthew.jobtracker.popups.NewSettingFragment

class TaskSettingsFragment : ListFragment<TaskSettingItemData, FragmentTaskSettingsBinding>("Task Settings"),
    DialogCallback {

    private val args : TaskSettingsFragmentArgs by navArgs()

    override fun getViewBinding(): FragmentTaskSettingsBinding {
        return FragmentTaskSettingsBinding.inflate(layoutInflater)
    }

    override fun setupViews(){
        _recyclerView = binding.rvPossibleTasks
        binding.fab.setOnClickListener { onFabPressed() }
    }

    override fun loadListData() {
        val possibleTasks = args.taskList.toMutableList()
        itemList = possibleTasks.map{ TaskSettingItemData(it) }
                as MutableList<TaskSettingItemData>
    }

    override fun onItemClick(position: Int) {}

    override fun onItemSwipe(position: Int) {
        removeTaskAtPos(position)
    }

    override fun onBackButtonPressed() {
        navigateBackToJobSettings()
    }

    override fun onFabPressed() {
        val newFragment = NewSettingFragment()
        newFragment.setTargetFragment(this, 0)
        newFragment.show(parentFragmentManager, "new_task_setting")
    }

    private fun navigateBackToJobSettings(){
        val action = TaskSettingsFragmentDirections.actionTaskSettingsMenuFragmentToJobSettingsMenuFragment()
        findNavController().navigate(action)
    }

    private fun removeTaskAtPos(position : Int){
        itemList.removeAt(position)

        val template = JobTemplate(args.job, itemList.map{it.text} as MutableList<String>)
        db.addJobTemplate(template)
    }

    override fun onDialogDismiss(response : String?){
        if(response == null || itemList.map{it.text}.contains(response)) return

        itemList.add(TaskSettingItemData(response))

        val template = JobTemplate(args.job, itemList.map{it.text} as MutableList<String>)
        db.addJobTemplate(template)

        recyclerView.adapter?.notifyDataSetChanged()
    }
}