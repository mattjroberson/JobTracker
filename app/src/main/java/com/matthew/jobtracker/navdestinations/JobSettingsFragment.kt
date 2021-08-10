package com.matthew.jobtracker.navdestinations

import androidx.navigation.fragment.findNavController
import com.matthew.jobtracker.helpers.DialogCallback
import com.matthew.jobtracker.data.rv_items.JobSettingItemData
import com.matthew.jobtracker.data.JobTemplate
import com.matthew.jobtracker.databinding.FragmentJobSettingsBinding
import com.matthew.jobtracker.popups.NewSettingFragment

class JobSettingsFragment :
    ListFragment<JobSettingItemData, FragmentJobSettingsBinding>("Jobs Settings"),
    DialogCallback {

    override fun getViewBinding(): FragmentJobSettingsBinding {
        return FragmentJobSettingsBinding.inflate(layoutInflater)
    }

    override fun setupViews(){
        _recyclerView = binding.rvPossibleJobs
        binding.fab.setOnClickListener { onFabPressed() }
    }

    override fun loadListData() {
        val templates = db.getTemplates()
        itemList = templates.map{JobSettingItemData(it)}
                as MutableList<JobSettingItemData>
    }

    override fun onItemClick(position : Int){
        val jobItem = itemList[position]
        val taskList = jobItem.template.taskTemplates.toTypedArray()

        val action = JobSettingsFragmentDirections.actionSettingsMenuFragmentToTaskSettingsMenuFragment(taskList, jobItem.template.name)
        findNavController().navigate(action)
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

        recyclerView.adapter?.notifyDataSetChanged()
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

