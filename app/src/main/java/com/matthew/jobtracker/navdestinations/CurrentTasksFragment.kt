package com.matthew.jobtracker.navdestinations

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.matthew.jobtracker.helpers.DialogCallback
import com.matthew.jobtracker.data.rv_items.CurrentTaskItemData
import com.matthew.jobtracker.popups.EditTaskFragment

class CurrentTasksFragment :
    ListFragment<CurrentTaskItemData>(), DialogCallback {

    private lateinit var jobName : String

    private val args : CurrentTasksFragmentArgs by navArgs()
    private var editingPosition : Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle("$jobName Tasks")
        setFabEnabled(false)
    }

    override fun getListData() : MutableList<CurrentTaskItemData> {
        jobName = args.jobName
        val job = db.getCurrentJob(jobName)

        if(job != null){
            return job.taskList.map{CurrentTaskItemData(it)}
                    as MutableList<CurrentTaskItemData>
        }

        return mutableListOf()
    }

    override fun onItemClick(position: Int) {
        val task = itemList[position].task
        editingPosition = position

        val newFragment = EditTaskFragment.newInstance(task.name, task.prettyTime)
        newFragment.setTargetFragment(this, 0)
        newFragment.show(parentFragmentManager,"edit task")
    }

    override fun onItemSwipe(position: Int) {
        removeTaskAtPos(position)
    }

    override fun onBackButtonPressed() {
        navigateBackToCurrentJobs()
    }

    override fun onDialogDismiss(response : String?){
        //Ignore if no response given
        if(response == null) return

        editTaskTimeAtPos(response, editingPosition)
        binding.recyclerView.adapter?.notifyItemChanged(editingPosition)
    }

    private fun removeTaskAtPos(position: Int){
        val job = db.getCurrentJob(jobName) ?: return
        job.taskList.removeAt(position)
        db.addCurrentJob(job)
        itemList.removeAt(position)
    }

    private fun editTaskTimeAtPos(newTime : String, position: Int){
        val job = db.getCurrentJob(jobName) ?: return
        job.taskList[position].loggedTime = newTime.toLong()
        itemList[position] = CurrentTaskItemData(job.taskList[position])
        db.addCurrentJob(job)
    }
    private fun navigateBackToCurrentJobs(){
        val action = CurrentTasksFragmentDirections.actionActiveTasksFragmentToActiveJobsFragment()
        findNavController().navigate(action)
    }
}