package com.matthew.jobtracker.navdestinations

import androidx.navigation.fragment.findNavController
import com.matthew.jobtracker.helpers.DialogCallback
import com.matthew.jobtracker.data.rv_items.JobSettingItemData
import com.matthew.jobtracker.data.JobTemplate
import com.matthew.jobtracker.popups.NewSettingFragment

class JobSettingsFragment :
    ListFragment<JobSettingItemData>("Jobs Settings"),
    DialogCallback {

    override fun getListData() : MutableList<JobSettingItemData> {
        val templates = db.getTemplates()
        return templates.map{JobSettingItemData(it)}
                as MutableList<JobSettingItemData>
    }

    override fun onItemClick(position : Int){
        val jobItem = itemList[position]
        navigateToTaskSetting(jobItem)
    }

    override fun onBackButtonPressed() {
        navBackToActiveJobs()
    }

    override fun onFabPressed() {
        val newFragment = NewSettingFragment()
        newFragment.setTargetFragment(this, 0)
        newFragment.show(parentFragmentManager, "new_job_setting")
    }

    override fun onItemSwipe(position: Int) {
        removeJobAtPos(position)
    }

    override fun onDialogDismiss(response : String?){
        //Ignore if no new name given or name already exists
        if(response == null || itemList.map{it.text}.contains(response)) return

        val newTemplate = JobTemplate(response)
        db.addJobTemplate(newTemplate)

        itemList.add(JobSettingItemData(newTemplate))

        binding.recyclerView.adapter?.notifyItemInserted(lastItemIndex)
    }

    private fun navigateToTaskSetting(jobItem : JobSettingItemData){
        val taskList = jobItem.template.taskTemplates.toTypedArray()

        val action = JobSettingsFragmentDirections.actionSettingsMenuFragmentToTaskSettingsMenuFragment(taskList, jobItem.template.name)
        findNavController().navigate(action)
    }

    private fun navBackToActiveJobs(){
        val action = JobSettingsFragmentDirections.actionJobSettingsMenuFragmentToActiveJobsFragment()
        findNavController().navigate(action)
    }

    private fun removeJobAtPos(position: Int){
        val name = itemList[position].template.name
        db.deleteJobTemplate(name)
        itemList.removeAt(position)
    }
}

